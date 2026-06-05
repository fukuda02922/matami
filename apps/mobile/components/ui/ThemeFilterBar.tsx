import React from 'react';
import { ScrollView, TouchableOpacity, Text, StyleSheet, View } from 'react-native';
import { Colors, Typography, Spacing, BorderRadius } from '@constants/theme';
import type { ThemeFilter, Theme } from '@types/index';
import { THEME_LABELS } from '@types/index';

interface ThemeFilterBarProps {
  selected: ThemeFilter;
  onSelect: (theme: ThemeFilter) => void;
}

const FILTERS: Array<{ key: ThemeFilter; label: string }> = [
  { key: 'all', label: 'すべて' },
  ...Object.entries(THEME_LABELS).map(([key, label]) => ({
    key: key as Theme,
    label,
  })),
];

export function ThemeFilterBar({ selected, onSelect }: ThemeFilterBarProps) {
  return (
    <View style={styles.wrapper}>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.content}
      >
        {FILTERS.map((filter) => {
          const isActive = selected === filter.key;
          return (
            <TouchableOpacity
              key={filter.key}
              style={[styles.chip, isActive && styles.chipActive]}
              onPress={() => onSelect(filter.key)}
              activeOpacity={0.7}
              accessibilityRole="button"
              accessibilityState={{ selected: isActive }}
            >
              <Text style={[styles.chipText, isActive && styles.chipTextActive]}>
                {filter.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    backgroundColor: Colors.bg,
    borderBottomWidth: 1,
    borderBottomColor: Colors.border,
  },
  content: {
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.md,
    gap: Spacing.sm,
  },
  chip: {
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.xs + 2,
    borderRadius: BorderRadius.full,
    backgroundColor: Colors.card,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  chipActive: {
    backgroundColor: Colors.primary,
    borderColor: Colors.primary,
  },
  chipText: {
    fontSize: Typography.fontSizeSM,
    fontWeight: Typography.fontWeightMedium,
    color: Colors.textMuted,
  },
  chipTextActive: {
    color: Colors.card,
  },
});

export default ThemeFilterBar;
