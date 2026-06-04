# インフラ設計

## MVP期（Railway中心）

```
[Expo App]
    │
    ├── HTTPS ──→ [Railway: Spring Boot API]
    │                   │
    │                   ├──→ [Supabase: PostgreSQL + pgvector]
    │                   ├──→ [Supabase Auth]
    │                   ├──→ [Upstash Redis]
    │                   └──→ [OpenAI API]
    │
    └── Auth ──→ [Supabase Auth]
```

## サービス一覧

| サービス | 用途 | Tier (MVP) | 月額目安 |
|---------|------|-----------|---------|
| Railway | Spring Boot ホスティング | Starter | ¥700〜 |
| Supabase | PostgreSQL + Auth | Free | ¥0 |
| Upstash | Redis キャッシュ | Free | ¥0 |
| Cloudflare R2 | 画像ストレージ | Free | ¥0 |
| OpenAI | AI機能 | Pay-as-you-go | ¥2,200〜 |
| Sentry | エラー監視 | Free | ¥0 |
| RevenueCat | 課金管理 | Free (〜$10k) | ¥0 |

## データベース設計

### スキーマ

```sql
-- ユーザー
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  supabase_uid UUID UNIQUE NOT NULL,
  age_range VARCHAR(10),           -- '20s', '30s', etc.
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 岐路（メイン投稿）
CREATE TABLE crossroads (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  theme VARCHAR(20) NOT NULL,      -- 'career', 'love', 'education', 'family', 'money', 'other'
  body TEXT NOT NULL,              -- 最大140字
  choice_a TEXT NOT NULL,
  choice_b TEXT NOT NULL,
  chosen CHAR(1) NOT NULL,        -- 'A' or 'B'
  age_at_time INT,
  embedding vector(1536),         -- text-embedding-3-small
  is_moderated BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- その後（共鳴投稿）
CREATE TABLE resonances (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  crossroads_id UUID REFERENCES crossroads(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  chosen CHAR(1) NOT NULL,
  after_text TEXT NOT NULL,
  years_later INT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- リアクション
CREATE TABLE reactions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  crossroads_id UUID REFERENCES crossroads(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  type VARCHAR(20) NOT NULL,       -- 'helpful', 'same_choice', 'different_choice'
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(crossroads_id, user_id, type)
);

-- 通報
CREATE TABLE reports (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  reporter_id UUID REFERENCES users(id),
  target_type VARCHAR(20) NOT NULL, -- 'crossroads', 'resonance'
  target_id UUID NOT NULL,
  reason VARCHAR(50),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- インデックス
CREATE INDEX ON crossroads USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
CREATE INDEX ON crossroads(theme, created_at DESC);
CREATE INDEX ON resonances(crossroads_id, created_at DESC);
```

## 環境変数

```bash
# .env.example
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJ...
SUPABASE_SERVICE_ROLE_KEY=eyJ...
OPENAI_API_KEY=sk-...
REDIS_URL=rediss://...
REVENUECAT_PUBLIC_KEY=appl_...
SENTRY_DSN=https://...
```

## スケールアップ時（AWS ECS）

DAU 10,000 超過時に Railway → AWS 移行を検討:

```
[CloudFront] → [ALB] → [ECS Fargate: Spring Boot]
                              │
                         [RDS Aurora PostgreSQL + pgvector]
                         [ElastiCache Redis]
                         [S3 + CloudFront: 画像]
```
