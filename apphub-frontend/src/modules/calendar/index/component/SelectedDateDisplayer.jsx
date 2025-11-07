const SelectedDateDispalyer = ({ referenceDate, view }) => {
    return (
        <div id="calendar-navigation-selected-date" className="nowrap">
            {view.format(referenceDate)}
        </div>
    );
}

export default SelectedDateDispalyer;