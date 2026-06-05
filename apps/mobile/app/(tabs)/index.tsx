import React, { useState, useCallback } from 'react';
import {
  FlatList,
  View,
  Text,
  StyleSheet,
  RefreshControl,
  ListRenderItemInfo,
} from 'react-native';
import { useInfiniteQuery } from '@tanstack/react-query';
import apiClient from '@lib/api';
import { CrossroadsCard } from '@components/crossroads/CrossroadsCard';
import { ThemeFilterBar } from '@components/ui/ThemeFilterBar';
import { LoadingSpinner } from '@components/ui/LoadingSpinner';
import { ErrorView } from '@components/ui/ErrorView';
import { Colors, Typography, Spacing } from '@constants/theme';
import type { Crossroads, PaginatedResponse, ThemeFilter } from '@types/index';

async function fetchCrossroads(
  page: number,
  theme: ThemeFilter
): Promise<PaginatedResponse<Crossroads>> {
  const params: Record<string, unknown> = { page, perPage: 20 };
  if (theme !== 'all') {
    params.theme = theme;
  }
  const { data } = await apiClient.get<PaginatedResponse<Crossroads>>('/crossroads', { params });
  return data;
}

function ItemSeparator() {
  return <View style={styles.separator} />;
}

function ListHeader() {
  return (
    <View style={styles.listHeader}>
      <Text style={styles.listHeaderText}>みんなの岐路</Text>
    </View>
  );
}

export default function HomeScreen() {
  const [themeFilter, setThemeFilter] = useState<ThemeFilter>('all');

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isError,
    refetch,
    isRefetching,
  } = useInfiniteQuery<PaginatedResponse<Crossroads>>({
    queryKey: ['crossroads', themeFilter],
    queryFn: ({ pageParam }) => fetchCrossroads(pageParam as number, themeFilter),
    initialPageParam: 1,
    getNextPageParam: (lastPage) => (lastPage.hasNext ? lastPage.page + 1 : undefined),
  });

  const allItems = data?.pages.flatMap((page) => page.items) ?? [];

  const handleLoadMore = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const handleThemeSelect = useCallback((theme: ThemeFilter) => {
    setThemeFilter(theme);
  }, []);

  const renderItem = useCallback(
    ({ item }: ListRenderItemInfo<Crossroads>) => (
      <CrossroadsCard crossroads={item} />
    ),
    []
  );

  const keyExtractor = useCallback((item: Crossroads) => item.id, []);

  const renderFooter = useCallback(() => {
    if (!isFetchingNextPage) return null;
    return <LoadingSpinner size="small" />;
  }, [isFetchingNextPage]);

  if (isLoading) {
    return (
      <>
        <ThemeFilterBar selected={themeFilter} onSelect={handleThemeSelect} />
        <LoadingSpinner fullScreen />
      </>
    );
  }

  if (isError) {
    return (
      <>
        <ThemeFilterBar selected={themeFilter} onSelect={handleThemeSelect} />
        <ErrorView onRetry={refetch} />
      </>
    );
  }

  return (
    <View style={styles.container}>
      <ThemeFilterBar selected={themeFilter} onSelect={handleThemeSelect} />
      <FlatList
        data={allItems}
        renderItem={renderItem}
        keyExtractor={keyExtractor}
        ItemSeparatorComponent={ItemSeparator}
        ListHeaderComponent={ListHeader}
        ListFooterComponent={renderFooter}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>まだ岐路がありません</Text>
          </View>
        }
        onEndReached={handleLoadMore}
        onEndReachedThreshold={0.5}
        refreshControl={
          <RefreshControl
            refreshing={isRefetching}
            onRefresh={refetch}
            tintColor={Colors.primary}
            colors={[Colors.primary]}
          />
        }
        contentContainerStyle={styles.listContent}
        showsVerticalScrollIndicator={false}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.bg,
  },
  listContent: {
    paddingBottom: Spacing.xxxl,
  },
  listHeader: {
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.lg,
    paddingBottom: Spacing.sm,
  },
  listHeaderText: {
    fontSize: Typography.fontSizeLG,
    fontWeight: Typography.fontWeightSemiBold,
    color: Colors.text,
  },
  separator: {
    height: Spacing.sm,
  },
  emptyContainer: {
    padding: Spacing.xxxl,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: Typography.fontSizeMD,
    color: Colors.textMuted,
  },
});
