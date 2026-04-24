create table article_views (
  id varchar(255) primary key,
  article_id varchar(255) not null,
  user_id varchar(255),
  viewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create index idx_article_views_article_id on article_views(article_id);
