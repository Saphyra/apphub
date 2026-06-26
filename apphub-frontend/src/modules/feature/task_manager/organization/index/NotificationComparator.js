/**
 * Compares two notifications.
 * If status is the same, compares by createdAt.
 * If status is different, UNREAD is considered greater than the others.
 *
 */
import { NotificationStatus } from "./notifications/NotificationStatus";

const notificationComparator = (a, b) => {
	if (a.status === b.status) {
		return b.createdAt - a.createdAt;
	}

	if (a.status === NotificationStatus.UNREAD) {
		return 1;
	}

	if (b.status === NotificationStatus.UNREAD) {
		return -1;
	}

	return b.createdAt - a.createdAt;
}

export default notificationComparator;