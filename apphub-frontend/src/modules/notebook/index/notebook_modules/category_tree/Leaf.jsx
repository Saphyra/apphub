import React from "react";
import Stream from "../../../../../common/js/collection/Stream";
import Button from "../../../../../common/component/input/Button";
import ListItemType from "../../../common/ListItemType";
import moveListItem from "../../../common/MoveListItemService";

const Leaf = ({ category, openedLeaves, setOpenedLeaves, setOpenedListItem, setLastEvent}) => {
    const hasLeaves = category.children.length > 0;
    const isOpened = openedLeaves.indexOf(category.categoryId) > -1;

    const getLeaves = () => {
        return (
            <div className="notebook-tree-leaf-children">
                {
                    new Stream(category.children)
                        .sorted((a, b) => a.title.localeCompare(b.title))
                        .map(leaf =>
                            <Leaf
                                key={leaf.categoryId}
                                category={leaf}
                                openedLeaves={openedLeaves}
                                setOpenedLeaves={setOpenedLeaves}
                                setOpenedListItem={setOpenedListItem}
                                setLastEvent={setLastEvent}
                            />
                        )
                        .toList()
                }
            </div>
        );
    }

    const closeButton = () => {
        const close = () => {
            const clone = new Stream(openedLeaves)
                .remove(item => item === category.categoryId)
                .toList();
            setOpenedLeaves(clone)
        }

        return <Button
            className="notebook-tree-leaf-button notebook-tree-leaf-close-button"
            label="^"
            onclick={close}
        />
    }

    const expandButton = () => {
        const expand = () => {
            const clone = new Stream(openedLeaves)
                .add(category.categoryId)
                .toList();
            setOpenedLeaves(clone)
        }

        return <Button
            className="notebook-tree-leaf-button notebook-tree-leaf-open-button"
            label="V"
            onclick={expand}
        />
    }

    //Drag & Drop
    const handleOnDragOver = (e) => {
        e.preventDefault();
    }

    const handleOnDrop = (e) => {
        const movedItemId = e.dataTransfer.getData("id");
        moveListItem(movedItemId, category.categoryId, setLastEvent);
    }

    return (
        <div
            className="notebook-tree-leaf-wrapper"
            onDragOver={handleOnDragOver}
            onDrop={handleOnDrop}
        >
            <div className="notebook-tree-leaf button">
                {hasLeaves && (isOpened ? closeButton() : expandButton())}

                <div
                    className="notebook-tree-leaf-title"
                    onClick={() => setOpenedListItem({ id: category.categoryId, type: ListItemType.CATEGORY })}
                >
                    {category.title}
                </div>
            </div>

            {isOpened && getLeaves()}
        </div>
    );
}

export default Leaf;