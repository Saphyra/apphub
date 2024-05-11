const ContactOrder = {
    DEFAULT: () => (a, b) => 0,
    CODE: () => (a, b) => a.code.localeCompare(b.code),
    NAME: () => (a, b) => a.name.localeCompare(b.name),
    PHONE: () => (a, b) => a.phone.localeCompare(b.phone),
    ADDRESS: () => (a, b) => a.address.localeCompare(b.address),
    NOTE: () => (a, b) => a.note.localeCompare(b.note),
}

export default ContactOrder;