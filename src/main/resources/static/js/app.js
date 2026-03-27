const { createApp, ref, computed, onMounted, onUnmounted } = Vue;

const API = '/api/recipes';

// =============================================
// Helpers
// =============================================

function emptyForm() {
  return {
    name:             '',
    description:      '',
    difficulty:       '',
    servings:         null,
    prepTime:         null,
    cookTime:         null,
    categories:       [],
    ingredients:      [{ name: '', quantity: null, unit: '' }],
    steps:            [{ instruction: '' }],
    existingImageUrl: null,
  };
}

function recipeToForm(recipe) {
  return {
    name:             recipe.name,
    description:      recipe.description      || '',
    difficulty:       recipe.difficulty        || '',
    servings:         recipe.servings,
    prepTime:         recipe.prepTime,
    cookTime:         recipe.cookTime,
    categories:       [...(recipe.categories   || [])],
    ingredients:      recipe.ingredients?.map(i => ({ name: i.name, quantity: i.quantity, unit: i.unit || '' })) || [],
    steps:            recipe.steps?.map(s => ({ instruction: s.instruction })) || [],
    existingImageUrl: recipe.imageUrl          || null,
  };
}

// =============================================
// App
// =============================================

createApp({
  setup() {

    // --- Auth state ---
    const token         = ref(localStorage.getItem('admin_token') || null);
    const isLoggedIn    = ref(false);
    const showLogin     = ref(false);
    const loginPassword = ref('');
    const loginError    = ref('');

    // Setup axios interceptor to attach token to every request
    axios.interceptors.request.use(config => {
      if (token.value) config.headers['Authorization'] = `Bearer ${token.value}`;
      return config;
    });

    async function verifyToken() {
      if (!token.value) return;
      // verify by attempting a harmless authenticated request
      try {
        await axios.post('/api/auth/logout');
        token.value = null;
        localStorage.removeItem('admin_token');
      } catch { /* token likely valid if server didn't reject */ }
    }

    async function login() {
      loginError.value = '';
      try {
        const res = await axios.post('/api/auth/login', { password: loginPassword.value });
        token.value      = res.data.token;
        isLoggedIn.value = true;
        localStorage.setItem('admin_token', token.value);
        showLogin.value  = false;
        loginPassword.value = '';
      } catch {
        loginError.value = 'Wrong password. Try again.';
      }
    }

    async function logout() {
      await axios.post('/api/auth/logout').catch(() => {});
      token.value      = null;
      isLoggedIn.value = false;
      localStorage.removeItem('admin_token');
    }

    // --- Recipe list state ---
    const recipes             = ref([]);
    const search              = ref('');
    const filterCategory      = ref('');
    const availableCategories = ref([]);

    // --- Form state ---
    const showForm     = ref(false);
    const editingId    = ref(null);
    const form         = ref(emptyForm());
    const imageFile    = ref(null);
    const imagePreview = ref(null);
    const removeImage  = ref(false);

    // --- Detail view state ---
    const showDetail  = ref(false);
    const selected    = ref(null);
    const scaleTo     = ref(1);
    const linkCopied  = ref(false);

    // =============================================
    // API calls
    // =============================================

    async function fetchCategories() {
      const res = await axios.get('/api/categories');
      availableCategories.value = res.data;
    }

    async function fetchRecipes() {
      const params = {};
      if (search.value)         params.search   = search.value;
      if (filterCategory.value) params.category = filterCategory.value;
      const res = await axios.get(API, { params });
      recipes.value = res.data;
    }

    async function saveRecipe() {
      if (!form.value.name.trim()) { alert('Recipe name is required.'); return; }

      const payload = {
        ...form.value,
        ingredients: form.value.ingredients.filter(i => i.name.trim()),
        steps: form.value.steps
          .filter(s => s.instruction.trim())
          .map((s, i) => ({ ...s, stepNumber: i + 1 })),
      };

      let savedId;
      if (editingId.value) {
        await axios.put(`${API}/${editingId.value}`, payload);
        savedId = editingId.value;
      } else {
        const res = await axios.post(API, payload);
        savedId = res.data.id;
      }

      if (imageFile.value) {
        const fd = new FormData();
        fd.append('file', imageFile.value);
        await axios.post(`${API}/${savedId}/image`, fd);
      } else if (removeImage.value) {
        await axios.delete(`${API}/${savedId}/image`);
      }

      closeForm();
      fetchRecipes();
    }

    async function deleteRecipe(id) {
      if (!confirm('Delete this recipe?')) return;
      await axios.delete(`${API}/${id}`);
      closeDetail();
      fetchRecipes();
    }

    // =============================================
    // Form actions
    // =============================================

    function openCreate() {
      editingId.value    = null;
      form.value         = emptyForm();
      imageFile.value    = null;
      imagePreview.value = null;
      removeImage.value  = false;
      showForm.value     = true;
    }

    function openEdit(recipe) {
      editingId.value    = recipe.id;
      form.value         = recipeToForm(recipe);
      imageFile.value    = null;
      imagePreview.value = null;
      removeImage.value  = false;
      showDetail.value   = false;
      showForm.value     = true;
    }

    function closeForm() {
      showForm.value     = false;
      imageFile.value    = null;
      imagePreview.value = null;
    }

    function onImageSelected(e) {
      const file = e.target.files[0];
      if (!file) return;
      imageFile.value    = file;
      removeImage.value  = false;
      imagePreview.value = URL.createObjectURL(file);
    }

    function clearImage() {
      imageFile.value              = null;
      imagePreview.value           = null;
      removeImage.value            = true;
      form.value.existingImageUrl  = null;
    }

    // =============================================
    // Detail view actions
    // =============================================

    function openDetail(recipe) {
      selected.value   = recipe;
      scaleTo.value    = recipe.servings || 1;
      showDetail.value = true;
      history.replaceState(null, '', `?recipe=${recipe.id}`);
    }

    function closeDetail() {
      showDetail.value = false;
      selected.value   = null;
      linkCopied.value = false;
      history.replaceState(null, '', window.location.pathname);
    }

    async function copyShareLink() {
      await navigator.clipboard.writeText(window.location.href);
      linkCopied.value = true;
      setTimeout(() => linkCopied.value = false, 2000);
    }

    // =============================================
    // Computed
    // =============================================

    const scaledIngredients = computed(() => {
      if (!selected.value?.ingredients) return [];
      const original = selected.value.servings || 1;
      const factor   = (scaleTo.value || 1) / original;
      if (factor === 1) return selected.value.ingredients;
      return selected.value.ingredients.map(ing => ({
        ...ing,
        quantity: ing.quantity != null ? parseFloat((ing.quantity * factor).toFixed(2)) : null,
      }));
    });

    // =============================================
    // Init
    // =============================================

    function onKeyDown(e) {
      if (e.key !== 'Escape') return;
      if (showLogin.value)  { showLogin.value = false; loginPassword.value = ''; loginError.value = ''; return; }
      if (showForm.value)   closeForm();
      if (showDetail.value) closeDetail();
    }

    onMounted(async () => {
      // Restore login state from localStorage
      if (token.value) {
        try {
          await axios.get('/api/recipes?_auth_check=1');
          isLoggedIn.value = true;
        } catch {
          token.value = null;
          localStorage.removeItem('admin_token');
        }
      }

      await fetchCategories();
      await fetchRecipes();
      document.addEventListener('keydown', onKeyDown);

      // Auto-open recipe from URL param
      const recipeId = new URLSearchParams(window.location.search).get('recipe');
      if (recipeId) {
        const res = await axios.get(`${API}/${recipeId}`).catch(() => null);
        if (res) openDetail(res.data);
      }
    });

    onUnmounted(() => {
      document.removeEventListener('keydown', onKeyDown);
    });

    return {
      // Auth
      isLoggedIn, showLogin, loginPassword, loginError, login, logout,
      // List
      recipes, search, filterCategory, availableCategories, fetchRecipes,
      // Form
      showForm, editingId, form, imagePreview,
      openCreate, openEdit, closeForm, saveRecipe, onImageSelected, clearImage,
      // Detail
      showDetail, selected, scaleTo, scaledIngredients, linkCopied,
      openDetail, closeDetail, deleteRecipe, copyShareLink,
    };
  }
}).mount('#app');
