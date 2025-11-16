import { Component } from '@angular/core';

interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  difficulty: string;
  image: string;
}


@Component({
  selector: 'app-products',
  imports: [],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class ProductsComponent {
  products: Product[] = [
    {
      id: 1,
      name: "Monstera Deliciosa",
      category: "indoor",
      price: 45.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop"
    },
    {
      id: 2,
      name: "Snake Plant",
      category: "indoor",
      price: 28.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1593482892540-3ba93169c797?w=400&h=300&fit=crop"
    },
    {
      id: 3,
      name: "Fiddle Leaf Fig",
      category: "indoor",
      price: 65.99,
      difficulty: "hard",
      image: "https://images.unsplash.com/photo-1521334884684-d80222895322?w=400&h=300&fit=crop"
    },
    {
      id: 4,
      name: "Succulent Garden",
      category: "succulents",
      price: 32.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=400&h=300&fit=crop"
    },
    {
      id: 5,
      name: "Peace Lily",
      category: "flowering",
      price: 38.99,
      difficulty: "medium",
      image: "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&h=300&fit=crop"
    },
    {
      id: 6,
      name: "Rubber Plant",
      category: "indoor",
      price: 42.99,
      difficulty: "medium",
      image: "https://images.unsplash.com/photo-1586611292717-f828b167408c?w=400&h=300&fit=crop"
    },
    {
      id: 7,
      name: "Lavender",
      category: "outdoor",
      price: 24.99,
      difficulty: "medium",
      image: "https://images.unsplash.com/photo-1611909023032-2d6b3134ecba?w=400&h=300&fit=crop"
    },
    {
      id: 8,
      name: "Aloe Vera",
      category: "succulents",
      price: 19.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1509423350716-97f2360af400?w=400&h=300&fit=crop"
    },
    {
      id: 9,
      name: "Bird of Paradise",
      category: "indoor",
      price: 78.99,
      difficulty: "hard",
      image: "https://images.unsplash.com/photo-1592150621744-aca64f48394a?w=400&h=300&fit=crop"
    },
    {
      id: 10,
      name: "Rose Bush",
      category: "flowering",
      price: 55.99,
      difficulty: "medium",
      image: "https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=300&fit=crop"
    },
    {
      id: 11,
      name: "Jade Plant",
      category: "succulents",
      price: 26.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=400&h=300&fit=crop"
    },
    {
      id: 12,
      name: "Pothos",
      category: "indoor",
      price: 22.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=400&h=300&fit=crop"
    },
    {
      id: 13,
      name: "ZZ Plant",
      category: "indoor",
      price: 34.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1545855572-1173b9e9e7a0?w=400&h=300&fit=crop"
    },
    {
      id: 14,
      name: "Cactus Mix",
      category: "succulents",
      price: 18.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1509423350716-97f2360af400?w=400&h=300&fit=crop"
    },
    {
      id: 15,
      name: "Philodendron",
      category: "indoor",
      price: 29.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=400&h=300&fit=crop"
    },
    {
      id: 16,
      name: "Orchid",
      category: "flowering",
      price: 48.99,
      difficulty: "hard",
      image: "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&h=300&fit=crop"
    },
    {
      id: 17,
      name: "Basil",
      category: "outdoor",
      price: 12.99,
      difficulty: "easy",
      image: "https://images.unsplash.com/photo-1611909023032-2d6b3134ecba?w=400&h=300&fit=crop"
    },
    {
      id: 18,
      name: "Fern",
      category: "indoor",
      price: 31.99,
      difficulty: "medium",
      image: "https://images.unsplash.com/photo-1521334884684-d80222895322?w=400&h=300&fit=crop"
    }
  ];

  categories = [
    { value: 'all', label: 'All' },
    { value: 'indoor', label: 'Indoor' },
    { value: 'outdoor', label: 'Outdoor' },
    { value: 'succulents', label: 'Succulents' },
    { value: 'flowering', label: 'Flowering' }
  ];

  difficulties = [
    { value: 'all', label: 'All' },
    { value: 'easy', label: 'Easy' },
    { value: 'medium', label: 'Medium' },
    { value: 'hard', label: 'Hard' }
  ];

  searchTerm = '';
  selectedCategory = 'all';
  selectedDifficulty = 'all';
  currentPage = 1;
  itemsPerPage = 6;

  filteredProducts: Product[] = [];
  paginatedProducts: Product[] = [];
  totalPages = 0;

  ngOnInit() {
    this.applyFilters();
  }

  onSearchChange() {
    this.currentPage = 1;
    this.applyFilters();
  }

  onCategoryChange(category: string) {
    this.selectedCategory = category;
    this.currentPage = 1;
    this.applyFilters();
  }

  onDifficultyChange(difficulty: string) {
    this.selectedDifficulty = difficulty;
    this.currentPage = 1;
    this.applyFilters();
  }

  applyFilters() {
    this.filteredProducts = this.products.filter(product => {
      const matchesCategory = this.selectedCategory === 'all' || product.category === this.selectedCategory;
      const matchesDifficulty = this.selectedDifficulty === 'all' || product.difficulty === this.selectedDifficulty;
      const matchesSearch = product.name.toLowerCase().includes(this.searchTerm.toLowerCase());

      return matchesCategory && matchesDifficulty && matchesSearch;
    });

    this.totalPages = Math.ceil(this.filteredProducts.length / this.itemsPerPage);
    this.updatePaginatedProducts();
  }

  updatePaginatedProducts() {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedProducts = this.filteredProducts.slice(startIndex, endIndex);
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePaginatedProducts();
    }
  }

  getPageNumbers(): (number | string)[] {
    const pages: (number | string)[] = [];

    for (let i = 1; i <= this.totalPages; i++) {
      if (i === 1 || i === this.totalPages || (i >= this.currentPage - 1 && i <= this.currentPage + 1)) {
        pages.push(i);
      } else if (i === this.currentPage - 2 || i === this.currentPage + 2) {
        pages.push('...');
      }
    }

    return pages;
  }

  getStartItem(): number {
    return (this.currentPage - 1) * this.itemsPerPage + 1;
  }

  getEndItem(): number {
    return Math.min(this.currentPage * this.itemsPerPage, this.filteredProducts.length);
  }

  addToCart(productId: number) {
    // Add to cart logic here
    console.log('Added product to cart:', productId);
  }

  protected readonly Number = Number;
}
