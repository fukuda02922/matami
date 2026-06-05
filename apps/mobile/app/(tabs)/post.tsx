import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  Alert,
} from 'react-native';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '@lib/api';
import { Colors, Typography, Spacing, BorderRadius, Shadows } from '@constants/theme';
import type { Crossroads, Theme } from '@types/index';
import { THEME_LABELS } from '@types/index';

interface PostCrossroadsPayload {
  theme: Theme;
  body: string;
  choiceA: string;
  choiceB: string;
  chosen: 'A' | 'B';
  ageAtTime?: number;
}

const THEMES = Object.entries(THEME_LABELS) as Array<[Theme, string]>;

export default function PostScreen() {
  const queryClient = useQueryClient();
  const [theme, setTheme] = useState<Theme>('career');
  const [body, setBody] = useState('');
  const [choiceA, setChoiceA] = useState('');
  const [choiceB, setChoiceB] = useState('');
  const [chosen, setChosen] = useState<'A' | 'B'>('A');
  const [ageAtTime, setAgeAtTime] = useState('');

  const mutation = useMutation({
    mutationFn: (payload: PostCrossroadsPayload) =>
      apiClient.post<Crossroads>('/crossroads', payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['crossroads'] });
      setBody('');
      setChoiceA('');
      setChoiceB('');
      setChosen('A');
      setAgeAtTime('');
      Alert.alert('投稿しました', '岐路を共有しました');
    },
    onError: () => {
      Alert.alert('エラー', '投稿に失敗しました。再度お試しください。');
    },
  });

  const handleSubmit = () => {
    if (!body.trim() || !choiceA.trim() || !choiceB.trim()) {
      Alert.alert('入力エラー', '本文と選択肢A・Bを入力してください');
      return;
    }
    mutation.mutate({
      theme,
      body: body.trim(),
      choiceA: choiceA.trim(),
      choiceB: choiceB.trim(),
      chosen,
      ageAtTime: ageAtTime ? Number(ageAtTime) : undefined,
    });
  };

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      keyboardShouldPersistTaps="handled"
    >
      <Text style={styles.sectionLabel}>テーマ</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.themeRow}>
        {THEMES.map(([key, label]) => (
          <TouchableOpacity
            key={key}
            style={[styles.themeChip, theme === key && styles.themeChipActive]}
            onPress={() => setTheme(key)}
          >
            <Text style={[styles.themeChipText, theme === key && styles.themeChipTextActive]}>
              {label}
            </Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      <Text style={styles.sectionLabel}>岐路の内容</Text>
      <TextInput
        style={[styles.input, styles.textArea]}
        placeholder="あのとき、〇〇と△△で迷っていました..."
        placeholderTextColor={Colors.textMuted}
        value={body}
        onChangeText={setBody}
        multiline
        numberOfLines={4}
        textAlignVertical="top"
      />

      <Text style={styles.sectionLabel}>選択肢</Text>
      <View style={styles.choicesRow}>
        <View style={styles.choiceInputWrapper}>
          <Text style={styles.choiceLabel}>A</Text>
          <TextInput
            style={[styles.input, styles.choiceInput]}
            placeholder="選択肢A"
            placeholderTextColor={Colors.textMuted}
            value={choiceA}
            onChangeText={setChoiceA}
          />
        </View>
        <View style={styles.choiceInputWrapper}>
          <Text style={styles.choiceLabel}>B</Text>
          <TextInput
            style={[styles.input, styles.choiceInput]}
            placeholder="選択肢B"
            placeholderTextColor={Colors.textMuted}
            value={choiceB}
            onChangeText={setChoiceB}
          />
        </View>
      </View>

      <Text style={styles.sectionLabel}>実際に選んだのは？</Text>
      <View style={styles.chosenRow}>
        {(['A', 'B'] as const).map((c) => (
          <TouchableOpacity
            key={c}
            style={[styles.chosenButton, chosen === c && styles.chosenButtonActive]}
            onPress={() => setChosen(c)}
          >
            <Text style={[styles.chosenButtonText, chosen === c && styles.chosenButtonTextActive]}>
              {c}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={styles.sectionLabel}>そのときの年齢（任意）</Text>
      <TextInput
        style={styles.input}
        placeholder="例: 22"
        placeholderTextColor={Colors.textMuted}
        value={ageAtTime}
        onChangeText={setAgeAtTime}
        keyboardType="numeric"
        maxLength={3}
      />

      <TouchableOpacity
        style={[styles.submitButton, mutation.isPending && styles.submitButtonDisabled]}
        onPress={handleSubmit}
        disabled={mutation.isPending}
        activeOpacity={0.8}
      >
        <Text style={styles.submitButtonText}>
          {mutation.isPending ? '投稿中...' : '岐路を投稿する'}
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.bg,
  },
  content: {
    padding: Spacing.lg,
    paddingBottom: Spacing.xxxl,
  },
  sectionLabel: {
    fontSize: Typography.fontSizeSM,
    fontWeight: Typography.fontWeightSemiBold,
    color: Colors.textMuted,
    marginTop: Spacing.xl,
    marginBottom: Spacing.sm,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  themeRow: {
    flexDirection: 'row',
    marginBottom: Spacing.sm,
  },
  themeChip: {
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.xs + 2,
    borderRadius: BorderRadius.full,
    backgroundColor: Colors.card,
    borderWidth: 1,
    borderColor: Colors.border,
    marginRight: Spacing.sm,
  },
  themeChipActive: {
    backgroundColor: Colors.primary,
    borderColor: Colors.primary,
  },
  themeChipText: {
    fontSize: Typography.fontSizeSM,
    color: Colors.textMuted,
  },
  themeChipTextActive: {
    color: Colors.card,
    fontWeight: Typography.fontWeightSemiBold,
  },
  input: {
    backgroundColor: Colors.card,
    borderWidth: 1,
    borderColor: Colors.border,
    borderRadius: BorderRadius.md,
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.md,
    fontSize: Typography.fontSizeMD,
    color: Colors.text,
    ...Shadows.card,
  },
  textArea: {
    height: 100,
    paddingTop: Spacing.md,
  },
  choicesRow: {
    flexDirection: 'row',
    gap: Spacing.sm,
  },
  choiceInputWrapper: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.sm,
  },
  choiceLabel: {
    fontSize: Typography.fontSizeMD,
    fontWeight: Typography.fontWeightBold,
    color: Colors.primary,
    width: 16,
  },
  choiceInput: {
    flex: 1,
  },
  chosenRow: {
    flexDirection: 'row',
    gap: Spacing.md,
  },
  chosenButton: {
    flex: 1,
    paddingVertical: Spacing.md,
    alignItems: 'center',
    borderRadius: BorderRadius.md,
    borderWidth: 2,
    borderColor: Colors.border,
    backgroundColor: Colors.card,
  },
  chosenButtonActive: {
    borderColor: Colors.primary,
    backgroundColor: Colors.primary + '15',
  },
  chosenButtonText: {
    fontSize: Typography.fontSizeLG,
    fontWeight: Typography.fontWeightBold,
    color: Colors.textMuted,
  },
  chosenButtonTextActive: {
    color: Colors.primary,
  },
  submitButton: {
    marginTop: Spacing.xxl,
    paddingVertical: Spacing.lg,
    backgroundColor: Colors.primary,
    borderRadius: BorderRadius.md,
    alignItems: 'center',
    ...Shadows.card,
  },
  submitButtonDisabled: {
    opacity: 0.6,
  },
  submitButtonText: {
    fontSize: Typography.fontSizeMD,
    fontWeight: Typography.fontWeightSemiBold,
    color: Colors.card,
  },
});
