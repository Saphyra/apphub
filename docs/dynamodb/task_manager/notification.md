# `apphub-task_manager-${Environment}-notification`

- `pk`: `USER#userId` (recipient)
- `sk`: `NOTIFICATION#notificationId`
- `status`: `enum: NotificationStatus`
- `notificationType`: `enum: NotificationType`
- `createdAt`: `timestamp`
- `lastModified`: `timestamp`
- `expiration`: `timestamp` (ttl)
- `data`: `JSON Map<String, String>`