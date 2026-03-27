const { createApp, ref, computed, onMounted } = Vue;

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
    const showDetail = ref(false);
    const selected   = ref(null);
    const scaleTo    = ref(1);

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
    }

    function closeDetail() {
      showDetail.value = false;
      selected.value   = null;
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

    onMounted(() => {
      fetchCategories();
      fetchRecipes();
    });

    return {
      // List
      recipes, search, filterCategory, availableCategories, fetchRecipes,
      // Form
      showForm, editingId, form, imagePreview,
      openCreate, openEdit, closeForm, saveRecipe, onImageSelected, clearImage,
      // Detail
      showDetail, selected, scaleTo, scaledIngredients,
      openDetail, closeDetail, deleteRecipe,
    };
  }
}).mount('#app');
