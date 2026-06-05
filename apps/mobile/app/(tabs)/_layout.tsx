import { Tabs } from 'expo-router';
import { Text } from 'react-native';
import { Colors, Typography } from '@constants/theme';

function TabIcon({ label, focused }: { label: string; focused: boolean }) {
  return (
    <Text
      style={{
        fontSize: Typography.fontSizeXS,
        color: focused ? Colors.primary : Colors.textMuted,
        marginTop: 2,
      }}
    >
      {label}
    </Text>
  );
}

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: Colors.primary,
        tabBarInactiveTintColor: Colors.textMuted,
        tabBarStyle: {
          backgroundColor: Colors.card,
          borderTopColor: Colors.border,
          borderTopWidth: 1,
          height: 80,
          paddingBottom: 16,
        },
        tabBarLabelStyle: {
          fontSize: Typography.fontSizeXS,
          fontWeight: Typography.fontWeightMedium,
        },
        headerStyle: { backgroundColor: Colors.bg },
        headerTintColor: Colors.text,
        headerTitleStyle: {
          fontWeight: Typography.fontWeightSemiBold,
          fontSize: Typography.fontSizeLG,
        },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: 'ホーム',
          tabBarIcon: ({ focused }) => <TabIcon label="🏠" focused={focused} />,
          headerTitle: 'また道',
        }}
      />
      <Tabs.Screen
        name="post"
        options={{
          title: '投稿',
          tabBarIcon: ({ focused }) => <TabIcon label="✏️" focused={focused} />,
          headerTitle: '岐路を投稿',
        }}
      />
      <Tabs.Screen
        name="resonance"
        options={{
          title: '共鳴',
          tabBarIcon: ({ focused }) => <TabIcon label="💫" focused={focused} />,
          headerTitle: '共鳴した岐路',
        }}
      />
      <Tabs.Screen
        name="my-crossroads"
        options={{
          title: 'マイ岐路',
          tabBarIcon: ({ focused }) => <TabIcon label="📖" focused={focused} />,
          headerTitle: 'マイ岐路',
        }}
      />
      <Tabs.Screen
        name="pattern"
        options={{
          title: 'パターン',
          tabBarIcon: ({ focused }) => <TabIcon label="📊" focused={focused} />,
          headerTitle: '私のパターン',
        }}
      />
    </Tabs>
  );
}
