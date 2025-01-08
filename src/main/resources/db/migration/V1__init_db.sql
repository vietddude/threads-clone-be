-- Table for user
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(255) NOT NULL,
                       fullname VARCHAR(255),
                       image VARCHAR(255),
                       bio TEXT,
                       link VARCHAR(255),
                       email VARCHAR(255) NOT NULL,
                       verified BOOLEAN,
                       privacy VARCHAR(255) DEFAULT 'PUBLIC',  -- Sử dụng VARCHAR thay vì ENUM
                       is_admin BOOLEAN DEFAULT false,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for follow
CREATE TABLE follows (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         follower_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                         followee_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT unique_follow UNIQUE (follower_id, followee_id)  -- Chỉ ràng buộc duy nhất
);

-- Table for post
CREATE TABLE posts (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       text TEXT NOT NULL,
                       image VARCHAR(255),
                       parent_post_id UUID REFERENCES posts(id) ON DELETE CASCADE,
                       quote_id UUID REFERENCES posts(id) ON DELETE CASCADE,
                       privacy VARCHAR(255) DEFAULT 'ANYONE',  -- Sử dụng VARCHAR thay vì ENUM
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for like
CREATE TABLE likes (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                      post_id UUID NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      CONSTRAINT unique_like UNIQUE (user_id, post_id)
);

-- Table for repost
CREATE TABLE reposts (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        post_id UUID NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT unique_repost UNIQUE (user_id, post_id)
);

-- Table for notification
CREATE TABLE notifications (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              read BOOLEAN DEFAULT false,
                              type notification_type NOT NULL,
                              message VARCHAR(255),
                              is_public BOOLEAN,
                              sender_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              receiver_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              post_id UUID REFERENCES posts (id) ON DELETE CASCADE,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for report
CREATE TABLE reports (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        reason VARCHAR(255),
                        post_id UUID NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
                        user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
-- Index on user.username for fast lookup by username
CREATE INDEX idx_user_username ON users (username);

-- Index on follow for frequent follower-followee lookups
CREATE INDEX idx_follow_follower ON follows(follower_id);
CREATE INDEX idx_follow_followee ON follows(followee_id);

-- Index on post.author_id for fast user-specific post queries
CREATE INDEX idx_post_author ON posts (author_id);

-- Index on like for fast like lookups
CREATE INDEX idx_like_user ON likes (user_id);
CREATE INDEX idx_like_post ON likes (post_id);

-- Index on notification.receiver_user_id for fast user-specific notifications
CREATE INDEX idx_notification_receiver ON notifications(receiver_user_id);
