# `apphub-${Environment}-user`

## Profile

- `pk`: `USER_ID#userId`
- `sk`: `PROFILE`
- `email`: `text`
- `username`: `text`
- `language`: `text`
- `password`: `text (hashed)`
- `password_failure_count`: `number`
- `locked_until`: `timestamp`

## Credential

Ensures one credential belongs to one user

- `pk`: `CREDENTIAL#username/email`
- `sk`: `CREDENTIAL`
- `user_id`: `USER_ID#userId`

## Role

- `pk`: `USER_ID#userId`
- `sk`: `ROLE#role`

## Marked for deletion

- `pk`: `USER_ID#userId`
- `sk`: `MARKED_FOR_DELETION`
- `marked_for_deletion_at`: `timestamp`

### marked_for_deletion_at-index

Query users marked for deletion

- `pk`: `MARKED_FOR_DELETION`
- `sk`: `marked_for_deletion_at`