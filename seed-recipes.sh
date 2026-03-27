#!/bin/bash

# Usage:
#   Local:      ./seed-recipes.sh
#   Production: ./seed-recipes.sh https://recipe-app-production-9416.up.railway.app

BASE_URL="${1:-http://localhost:8080}"
API="$BASE_URL/api/recipes"

post() {
  curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$API" \
    -H "Content-Type: application/json" \
    -d "$1"
}

echo "Seeding recipes to $API..."

# 1 — Spaghetti Carbonara
STATUS=$(post '{
  "name": "Spaghetti Carbonara",
  "description": "A classic Roman pasta dish with crispy pancetta, eggs, and Parmesan.",
  "difficulty": "Medium",
  "servings": 4,
  "prepTime": 10,
  "cookTime": 20,
  "categories": ["Pasta", "Dinner"],
  "ingredients": [
    { "name": "Spaghetti",         "quantity": 400,  "unit": "g"   },
    { "name": "Pancetta",          "quantity": 150,  "unit": "g"   },
    { "name": "Eggs",              "quantity": 4,    "unit": ""    },
    { "name": "Parmesan",          "quantity": 80,   "unit": "g"   },
    { "name": "Black pepper",      "quantity": 1,    "unit": "tsp" },
    { "name": "Salt",              "quantity": 1,    "unit": "tsp" }
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Cook spaghetti in salted boiling water until al dente." },
    { "stepNumber": 2, "instruction": "Fry pancetta in a pan until crispy, then remove from heat." },
    { "stepNumber": 3, "instruction": "Whisk eggs with grated Parmesan and plenty of black pepper." },
    { "stepNumber": 4, "instruction": "Drain pasta, reserving 1 cup of pasta water." },
    { "stepNumber": 5, "instruction": "Toss hot pasta with pancetta, then add egg mixture off heat, stirring quickly. Add pasta water as needed to create a creamy sauce." }
  ]
}')
echo "1. Spaghetti Carbonara → $STATUS"

# 2 — Avocado Toast
STATUS=$(post '{
  "name": "Avocado Toast",
  "description": "Quick and nutritious breakfast with creamy avocado on sourdough.",
  "difficulty": "Easy",
  "servings": 2,
  "prepTime": 5,
  "cookTime": 5,
  "categories": ["Breakfast"],
  "ingredients": [
    { "name": "Sourdough bread",   "quantity": 2,    "unit": "slices" },
    { "name": "Avocado",           "quantity": 1,    "unit": ""       },
    { "name": "Lemon juice",       "quantity": 1,    "unit": "tbsp"   },
    { "name": "Red chili flakes",  "quantity": 0.5,  "unit": "tsp"    },
    { "name": "Salt",              "quantity": 0.5,  "unit": "tsp"    },
    { "name": "Olive oil",         "quantity": 1,    "unit": "tbsp"   }
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Toast the sourdough slices until golden and crispy." },
    { "stepNumber": 2, "instruction": "Mash avocado with lemon juice, salt, and chili flakes." },
    { "stepNumber": 3, "instruction": "Spread avocado on toast and drizzle with olive oil." }
  ]
}')
echo "2. Avocado Toast → $STATUS"

# 3 — Caesar Salad
STATUS=$(post '{
  "name": "Caesar Salad",
  "description": "Crisp romaine lettuce with homemade Caesar dressing and crunchy croutons.",
  "difficulty": "Easy",
  "servings": 4,
  "prepTime": 15,
  "cookTime": 10,
  "categories": ["Salad", "Lunch"],
  "ingredients": [
    { "name": "Romaine lettuce",   "quantity": 1,    "unit": "head"  },
    { "name": "Parmesan",          "quantity": 50,   "unit": "g"     },
    { "name": "Bread",             "quantity": 2,    "unit": "slices"},
    { "name": "Garlic",            "quantity": 2,    "unit": "cloves"},
    { "name": "Anchovy paste",     "quantity": 1,    "unit": "tsp"   },
    { "name": "Mayonnaise",        "quantity": 3,    "unit": "tbsp"  },
    { "name": "Lemon juice",       "quantity": 2,    "unit": "tbsp"  },
    { "name": "Worcestershire",    "quantity": 1,    "unit": "tsp"   }
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Cube bread, toss with olive oil and garlic, bake at 200°C for 10 min until golden." },
    { "stepNumber": 2, "instruction": "Mix mayonnaise, lemon juice, anchovy paste, and Worcestershire for the dressing." },
    { "stepNumber": 3, "instruction": "Tear romaine into pieces and toss with dressing." },
    { "stepNumber": 4, "instruction": "Top with croutons and shaved Parmesan." }
  ]
}')
echo "3. Caesar Salad → $STATUS"

# 4 — Chocolate Chip Cookies
STATUS=$(post '{
  "name": "Chocolate Chip Cookies",
  "description": "Soft and chewy classic cookies loaded with chocolate chips.",
  "difficulty": "Easy",
  "servings": 24,
  "prepTime": 15,
  "cookTime": 12,
  "categories": ["Baking", "Dessert"],
  "ingredients": [
    { "name": "All-purpose flour", "quantity": 280,  "unit": "g"   },
    { "name": "Butter",            "quantity": 225,  "unit": "g"   },
    { "name": "Brown sugar",       "quantity": 200,  "unit": "g"   },
    { "name": "White sugar",       "quantity": 100,  "unit": "g"   },
    { "name": "Eggs",              "quantity": 2,    "unit": ""    },
    { "name": "Vanilla extract",   "quantity": 2,    "unit": "tsp" },
    { "name": "Baking soda",       "quantity": 1,    "unit": "tsp" },
    { "name": "Salt",              "quantity": 1,    "unit": "tsp" },
    { "name": "Chocolate chips",   "quantity": 300,  "unit": "g"   }
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Preheat oven to 190°C. Line baking sheets with parchment paper." },
    { "stepNumber": 2, "instruction": "Beat butter and both sugars together until light and fluffy." },
    { "stepNumber": 3, "instruction": "Add eggs and vanilla, mix until combined." },
    { "stepNumber": 4, "instruction": "Stir in flour, baking soda, and salt until just combined." },
    { "stepNumber": 5, "instruction": "Fold in chocolate chips." },
    { "stepNumber": 6, "instruction": "Scoop tablespoon-sized balls onto baking sheet and bake 10–12 min until edges are golden." }
  ]
}')
echo "4. Chocolate Chip Cookies → $STATUS"

# 5 — Tomato Soup
STATUS=$(post '{
  "name": "Creamy Tomato Soup",
  "description": "Velvety roasted tomato soup perfect with crusty bread.",
  "difficulty": "Easy",
  "servings": 4,
  "prepTime": 10,
  "cookTime": 40,
  "categories": ["Soup", "Lunch"],
  "ingredients": [
    { "name": "Tomatoes",          "quantity": 1000, "unit": "g"   },
    { "name": "Onion",             "quantity": 1,    "unit": ""    },
    { "name": "Garlic",            "quantity": 4,    "unit": "cloves"},
    { "name": "Vegetable broth",   "quantity": 500,  "unit": "ml"  },
    { "name": "Heavy cream",       "quantity": 100,  "unit": "ml"  },
    { "name": "Olive oil",         "quantity": 3,    "unit": "tbsp"},
    { "name": "Sugar",             "quantity": 1,    "unit": "tsp" },
    { "name": "Salt and pepper",   "quantity": 1,    "unit": "tsp" }
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Halve tomatoes, toss with olive oil and garlic, roast at 200°C for 25 min." },
    { "stepNumber": 2, "instruction": "Sauté diced onion in a pot until soft." },
    { "stepNumber": 3, "instruction": "Add roasted tomatoes and broth, simmer for 15 min." },
    { "stepNumber": 4, "instruction": "Blend until smooth, then stir in cream, sugar, salt and pepper." }
  ]
}')
echo "5. Creamy Tomato Soup → $STATUS"

# 6 — Banana Pancakes
STATUS=$(post '{
  "name": "Fluffy Banana Pancakes",
  "description": "Light and fluffy pancakes naturally sweetened with ripe banana.",
  "difficulty": "Easy",
  "servings": 2,
  "prepTime": 5,
  "cookTime": 15,
  "categories": ["Breakfast"],
  "ingredients": [
    { "name": "Ripe bananas",      "quantity": 2,    "unit": ""    },
    { "name": "Eggs",              "quantity": 2,    "unit": ""    },
    { "name": "All-purpose flour", "quantity": 120,  "unit": "g"   },
    { "name": "Milk",              "quantity": 120,  "unit": "ml"  },
    { "name": "Baking powder",     "quantity": 1,    "unit": "tsp" },
    { "name": "Butter",            "quantity": 1,    "unit": "tbsp"},
    { "name": "Pinch of salt",     "quantity": 1,    "unit": "pinch"}
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Mash bananas well in a bowl." },
    { "stepNumber": 2, "instruction": "Whisk in eggs and milk." },
    { "stepNumber": 3, "instruction": "Fold in flour, baking powder, and salt until just combined. Do not overmix." },
    { "stepNumber": 4, "instruction": "Melt butter in a non-stick pan over medium heat. Cook pancakes 2–3 min per side." }
  ]
}')
echo "6. Fluffy Banana Pancakes → $STATUS"

# 7 — Grilled Chicken
STATUS=$(post '{
  "name": "Herb Grilled Chicken",
  "description": "Juicy grilled chicken marinated in garlic, lemon, and fresh herbs.",
  "difficulty": "Medium",
  "servings": 4,
  "prepTime": 15,
  "cookTime": 20,
  "categories": ["Dinner"],
  "ingredients": [
    { "name": "Chicken breasts",   "quantity": 4,    "unit": ""    },
    { "name": "Olive oil",         "quantity": 4,    "unit": "tbsp"},
    { "name": "Garlic",            "quantity": 4,    "unit": "cloves"},
    { "name": "Lemon juice",       "quantity": 3,    "unit": "tbsp"},
    { "name": "Fresh rosemary",    "quantity": 2,    "unit": "tbsp"},
    { "name": "Fresh thyme",       "quantity": 2,    "unit": "tbsp"},
    { "name": "Salt",              "quantity": 1,    "unit": "tsp" },
    { "name": "Black pepper",      "quantity": 1,    "unit": "tsp" }
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Combine olive oil, garlic, lemon juice, herbs, salt and pepper for the marinade." },
    { "stepNumber": 2, "instruction": "Coat chicken and marinate for at least 30 minutes (or overnight)." },
    { "stepNumber": 3, "instruction": "Grill over medium-high heat for 6–7 min per side until cooked through." },
    { "stepNumber": 4, "instruction": "Rest for 5 min before serving." }
  ]
}')
echo "7. Herb Grilled Chicken → $STATUS"

# 8 — Greek Salad
STATUS=$(post '{
  "name": "Greek Salad",
  "description": "A refreshing Mediterranean salad with feta, olives, and crisp vegetables.",
  "difficulty": "Easy",
  "servings": 4,
  "prepTime": 15,
  "cookTime": 0,
  "categories": ["Salad", "Lunch"],
  "ingredients": [
    { "name": "Tomatoes",          "quantity": 3,    "unit": ""    },
    { "name": "Cucumber",          "quantity": 1,    "unit": ""    },
    { "name": "Red onion",         "quantity": 0.5,  "unit": ""    },
    { "name": "Feta cheese",       "quantity": 200,  "unit": "g"   },
    { "name": "Kalamata olives",   "quantity": 100,  "unit": "g"   },
    { "name": "Olive oil",         "quantity": 4,    "unit": "tbsp"},
    { "name": "Dried oregano",     "quantity": 1,    "unit": "tsp" },
    { "name": "Salt and pepper",   "quantity": 1,    "unit": "tsp" }
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Chop tomatoes, cucumber, and red onion into chunks." },
    { "stepNumber": 2, "instruction": "Combine vegetables and olives in a bowl." },
    { "stepNumber": 3, "instruction": "Drizzle with olive oil, sprinkle with oregano, salt and pepper." },
    { "stepNumber": 4, "instruction": "Place feta block on top and serve immediately." }
  ]
}')
echo "8. Greek Salad → $STATUS"

# 9 — Tiramisu
STATUS=$(post '{
  "name": "Tiramisu",
  "description": "The iconic Italian dessert with layers of espresso-soaked ladyfingers and mascarpone cream.",
  "difficulty": "Medium",
  "servings": 8,
  "prepTime": 30,
  "cookTime": 0,
  "categories": ["Dessert"],
  "ingredients": [
    { "name": "Ladyfinger biscuits","quantity": 300,  "unit": "g"   },
    { "name": "Mascarpone",        "quantity": 500,  "unit": "g"   },
    { "name": "Eggs",              "quantity": 4,    "unit": ""    },
    { "name": "Sugar",             "quantity": 100,  "unit": "g"   },
    { "name": "Espresso",          "quantity": 300,  "unit": "ml"  },
    { "name": "Cocoa powder",      "quantity": 3,    "unit": "tbsp"},
    { "name": "Rum (optional)",    "quantity": 2,    "unit": "tbsp"}
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Separate eggs. Beat yolks with sugar until pale and thick." },
    { "stepNumber": 2, "instruction": "Fold mascarpone into the yolk mixture until smooth." },
    { "stepNumber": 3, "instruction": "Whip egg whites to stiff peaks and gently fold into mascarpone mixture." },
    { "stepNumber": 4, "instruction": "Mix espresso with rum. Quickly dip ladyfingers and layer in a dish." },
    { "stepNumber": 5, "instruction": "Spread half the cream over the ladyfingers. Repeat layers." },
    { "stepNumber": 6, "instruction": "Dust generously with cocoa powder. Refrigerate for at least 4 hours." }
  ]
}')
echo "9. Tiramisu → $STATUS"

# 10 — Minestrone
STATUS=$(post '{
  "name": "Minestrone Soup",
  "description": "Hearty Italian vegetable soup with pasta and beans.",
  "difficulty": "Easy",
  "servings": 6,
  "prepTime": 20,
  "cookTime": 40,
  "categories": ["Soup", "Dinner"],
  "ingredients": [
    { "name": "Canned tomatoes",   "quantity": 400,  "unit": "g"   },
    { "name": "Cannellini beans",  "quantity": 400,  "unit": "g"   },
    { "name": "Zucchini",          "quantity": 1,    "unit": ""    },
    { "name": "Carrots",           "quantity": 2,    "unit": ""    },
    { "name": "Celery",            "quantity": 2,    "unit": "stalks"},
    { "name": "Onion",             "quantity": 1,    "unit": ""    },
    { "name": "Garlic",            "quantity": 3,    "unit": "cloves"},
    { "name": "Small pasta",       "quantity": 100,  "unit": "g"   },
    { "name": "Vegetable broth",   "quantity": 1500, "unit": "ml"  },
    { "name": "Olive oil",         "quantity": 3,    "unit": "tbsp"},
    { "name": "Parmesan rind",     "quantity": 1,    "unit": "piece"}
  ],
  "steps": [
    { "stepNumber": 1, "instruction": "Sauté onion, carrot, celery, and garlic in olive oil until softened." },
    { "stepNumber": 2, "instruction": "Add zucchini, canned tomatoes, and broth. Add the Parmesan rind." },
    { "stepNumber": 3, "instruction": "Simmer for 25 min, then add beans and pasta." },
    { "stepNumber": 4, "instruction": "Cook until pasta is tender, about 10 min. Remove Parmesan rind and serve." }
  ]
}')
echo "10. Minestrone Soup → $STATUS"

echo ""
echo "Done!"
