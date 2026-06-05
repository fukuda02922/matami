import { useQuery, useMutation, useInfiniteQuery, QueryKey, UseQueryOptions, UseMutationOptions, UseInfiniteQueryOptions, InfiniteData } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import apiClient from '@lib/api';
import type { PaginatedResponse } from '@types/index';

export function useGet<T>(
  queryKey: QueryKey,
  url: string,
  options?: Omit<UseQueryOptions<T, AxiosError>, 'queryKey' | 'queryFn'>
) {
  return useQuery<T, AxiosError>({
    queryKey,
    queryFn: async () => {
      const { data } = await apiClient.get<T>(url);
      return data;
    },
    ...options,
  });
}

export function usePost<TData, TVariables>(
  url: string,
  options?: UseMutationOptions<TData, AxiosError, TVariables>
) {
  return useMutation<TData, AxiosError, TVariables>({
    mutationFn: async (variables) => {
      const { data } = await apiClient.post<TData>(url, variables);
      return data;
    },
    ...options,
  });
}

export function usePatch<TData, TVariables>(
  url: string,
  options?: UseMutationOptions<TData, AxiosError, TVariables>
) {
  return useMutation<TData, AxiosError, TVariables>({
    mutationFn: async (variables) => {
      const { data } = await apiClient.patch<TData>(url, variables);
      return data;
    },
    ...options,
  });
}

export function useDelete<TData>(
  url: string,
  options?: UseMutationOptions<TData, AxiosError, void>
) {
  return useMutation<TData, AxiosError, void>({
    mutationFn: async () => {
      const { data } = await apiClient.delete<TData>(url);
      return data;
    },
    ...options,
  });
}

export function usePaginatedGet<T>(
  queryKey: QueryKey,
  url: string,
  options?: Omit<
    UseInfiniteQueryOptions<PaginatedResponse<T>, AxiosError, InfiniteData<PaginatedResponse<T>>, PaginatedResponse<T>, QueryKey, number>,
    'queryKey' | 'queryFn' | 'initialPageParam' | 'getNextPageParam'
  >
) {
  return useInfiniteQuery<PaginatedResponse<T>, AxiosError, InfiniteData<PaginatedResponse<T>>, QueryKey, number>({
    queryKey,
    queryFn: async ({ pageParam = 1 }) => {
      const { data } = await apiClient.get<PaginatedResponse<T>>(url, {
        params: { page: pageParam, perPage: 20 },
      });
      return data;
    },
    initialPageParam: 1,
    getNextPageParam: (lastPage) =>
      lastPage.hasNext ? lastPage.page + 1 : undefined,
    ...options,
  });
}
