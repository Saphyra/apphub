const SelectedDateDispalyer = ({ referenceDate, view }) => {
    return (
        <div id="calendar-navigation-selected-date">
            {view.format(referenceDate)}
        </div>
    );
}

export default SelectedDateDispalyer;