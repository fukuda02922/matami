import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Colors, Typography, Spacing, BorderRadius, Shadows } from '@constants/theme';
import type { Crossroads, Theme } from '@types/index';
import { THEME_LABELS } from '@types/index';

interface CrossroadsCardProps {
  crossroads: Crossroads;
  onPress?: (crossroads: Crossroads) => void;
}

const THEME_COLORS: Record<Theme, string> = {
  career: '#5C6BC0',
  love: '#E91E63',
  education: '#009688',
  family: '#FF9800',
  money: '#4CAF50',
  other: '#9E9E9E',
};

export function CrossroadsCard({ crossroads, onPress }: CrossroadsCardProps) {
  const totalVotes = crossroads.countA + crossroads.countB;
  const ratioA = totalVotes > 0 ? (crossroads.countA / totalVotes) * 100 : 50;
  const ratioB = totalVotes > 0 ? (crossroads.countB / totalVotes) * 100 : 50;
  const themeColor = THEME_COLORS[crossroads.theme];

  return (
    <TouchableOpacity
      style={styles.container}
      onPress={() => onPress?.(crossroads)}
      activeOpacity={0.8}
      accessibilityRole="button"
      accessibilityLabel={`岐路: ${crossroads.body}`}
    >
      <View style={styles.header}>
        <View style={[styles.themeBadge, { backgroundColor: themeColor + '20' }]}>
          <Text style={[styles.themeLabel, { color: themeColor }]}>
            {THEME_LABELS[crossroads.theme]}
          </Text>
        </View>
        {crossroads.ageAtTime !== undefined && (
          <Text style={styles.age}>{crossroads.ageAtTime}歳のとき</Text>
        )}
      </View>

      <Text style={styles.body} numberOfLines={3} testID="crossroads-body">
        {crossroads.body}
      </Text>

      <View style={styles.choices}>
        <View style={[styles.choiceChip, styles.choiceA]}>
          <Text style={styles.choiceLabel}>A</Text>
          <Text style={styles.choiceText} numberOfLines={1} testID="crossroads-choice-a">
            {crossroads.choiceA}
          </Text>
        </View>
        <View style={[styles.choiceChip, styles.choiceB]}>
          <Text style={styles.choiceLabel}>B</Text>
          <Text style={styles.choiceText} numberOfLines={1} testID="crossroads-choice-b">
            {crossroads.choiceB}
          </Text>
        </View>
      </View>

      <View style={styles.voteBar}>
        <View style={[styles.voteSegmentA, { flex: ratioA }]} />
        <View style={[styles.voteSegmentB, { flex: ratioB }]} />
      </View>

      <View style={styles.footer}>
        <Text style={styles.footerText}>
          A {Math.round(ratioA)}% · B {Math.round(ratioB)}%
        </Text>
        <Text style={styles.footerText}>
          💫 {crossroads.resonanceCount}
        </Text>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.card,
    borderRadius: BorderRadius.lg,
    padding: Spacing.lg,
    marginHorizontal: Spacing.lg,
    marginVertical: Spacing.sm,
    ...Shadows.card,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: Spacing.md,
  },
  themeBadge: {
    paddingHorizontal: Spacing.sm,
    paddingVertical: Spacing.xs,
    borderRadius: BorderRadius.full,
  },
  themeLabel: {
    fontSize: Typography.fontSizeXS,
    fontWeight: Typography.fontWeightSemiBold,
  },
  age: {
    fontSize: Typography.fontSizeXS,
    color: Colors.textMuted,
  },
  body: {
    fontSize: Typography.fontSizeMD,
    fontWeight: Typography.fontWeightMedium,
    color: Colors.text,
    lineHeight: Typography.lineHeightLG,
    marginBottom: Spacing.lg,
  },
  choices: {
    flexDirection: 'row',
    gap: Spacing.sm,
    marginBottom: Spacing.md,
  },
  choiceChip: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.xs,
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    borderRadius: BorderRadius.md,
  },
  choiceA: {
    backgroundColor: Colors.primary + '15',
  },
  choiceB: {
    backgroundColor: Colors.accent + '15',
  },
  choiceLabel: {
    fontSize: Typography.fontSizeSM,
    fontWeight: Typography.fontWeightBold,
    color: Colors.textMuted,
  },
  choiceText: {
    flex: 1,
    fontSize: Typography.fontSizeSM,
    color: Colors.text,
  },
  voteBar: {
    flexDirection: 'row',
    height: 4,
    borderRadius: BorderRadius.full,
    overflow: 'hidden',
    backgroundColor: Colors.border,
    marginBottom: Spacing.sm,
  },
  voteSegmentA: {
    backgroundColor: Colors.primary,
  },
  voteSegmentB: {
    backgroundColor: Colors.accent,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  footerText: {
    fontSize: Typography.fontSizeXS,
    color: Colors.textMuted,
  },
});

export default CrossroadsCard;
