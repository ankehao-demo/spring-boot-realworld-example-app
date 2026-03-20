export interface User {
  email: string;
  username: string;
  bio: string | null;
  image: string | null;
}

export interface UserWithToken extends User {
  token: string;
}

export interface Profile {
  username: string;
  bio: string | null;
  image: string | null;
  following: boolean;
}

export interface Article {
  slug: string;
  title: string;
  description: string;
  body: string;
  tagList: string[];
  createdAt: string;
  updatedAt: string;
  favorited: boolean;
  favoritesCount: number;
  author: Profile;
}

export interface Comment {
  id: string;
  createdAt: string;
  updatedAt: string;
  body: string;
  author: Profile;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  username: string;
  email: string;
  password: string;
}

export interface NewArticle {
  title: string;
  description: string;
  body: string;
  tagList: string[];
}

export interface UpdateUser {
  email?: string;
  username?: string;
  password?: string;
  image?: string;
  bio?: string;
}

export interface ArticlesParams {
  tag?: string;
  author?: string;
  favorited?: string;
  limit?: number;
  offset?: number;
}

export interface ArticlesResponse {
  articles: Article[];
  articlesCount: number;
}
