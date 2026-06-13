# `apphub-${Environment}-stored_file`

- `userId`: `uuid` (pk)
- `storedFileId`: `uuid` (sk)
- `fileName`: `text` (encrypted)
- `size`: `number` (encrypted)
- `createdAt`: `timestamp`
- `expiration`: `timestamp` (ttl, optional, for temporary files)
- `storage`: `StorageType`