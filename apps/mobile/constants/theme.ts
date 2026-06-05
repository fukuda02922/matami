export const Colors = {
  primary: '#5C6BC0',
  primaryLight: '#7986CB',
  primaryDark: '#3949AB',
  accent: '#FF7043',
  bg: '#F5F5F7',
  card: '#FFFFFF',
  text: '#1A1A2E',
  textMuted: '#666680',
  border: '#E0E0EE',

  success: '#4CAF50',
  warning: '#FF9800',
  error: '#F44336',

  choiceA: '#5C6BC0',
  choiceB: '#FF7043',
} as const;

export const Typography = {
  fontSizeXS: 11,
  fontSizeSM: 13,
  fontSizeMD: 15,
  fontSizeLG: 17,
  fontSizeXL: 20,
  fontSize2XL: 24,
  fontSize3XL: 30,

  fontWeightRegular: '400' as const,
  fontWeightMedium: '500' as const,
  fontWeightSemiBold: '600' as const,
  fontWeightBold: '700' as const,

  lineHeightSM: 18,
  lineHeightMD: 22,
  lineHeightLG: 26,
  lineHeightXL: 32,
} as const;

export const Spacing = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  xxl: 24,
  xxxl: 32,
} as const;

export const BorderRadius = {
  sm: 6,
  md: 10,
  lg: 14,
  xl: 20,
  full: 9999,
} as const;

export const Shadows = {
  card: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.06,
    shadowRadius: 8,
    elevation: 3,
  },
  modal: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.12,
    shadowRadius: 24,
    elevation: 8,
  },
} as const;
