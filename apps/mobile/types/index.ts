export type Theme = 'career' | 'love' | 'education' | 'family' | 'money' | 'other';

export interface Crossroads {
  id: string;
  theme: Theme;
  body: string;
  choiceA: string;
  choiceB: string;
  chosen: 'A' | 'B';
  ageAtTime?: number;
  countA: number;
  countB: number;
  resonanceCount: number;
  createdAt: string;
}

export interface Resonance {
  id: string;
  crossroadsId: string;
  chosen: 'A' | 'B';
  afterText: string;
  yearsLater?: number;
  createdAt: string;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  perPage: number;
  hasNext: boolean;
}

export interface ApiError {
  message: string;
  code: string;
  status: number;
}

export type ThemeFilter = Theme | 'all';

export const THEME_LABELS: Record<Theme, string> = {
  career: 'キャリア',
  love: '恋愛',
  education: '学業',
  family: '家族',
  money: 'お金',
  other: 'その他',
};
