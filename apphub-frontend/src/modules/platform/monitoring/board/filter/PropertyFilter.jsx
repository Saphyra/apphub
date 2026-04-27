import InputField from "common/component/input/InputField";
import PostLabeledInputField from "common/component/input/PostLabeledInputField";
import Stream from "common/js/collection/Stream";
import { addAndSet, removeAndSet } from "common/js/Utils";
import "./property_filter.css";

const PropertyFilter = ({ metrics, hiddenProperties, setHiddenProperties }) => {
    const properties = new Stream(metrics)
        .flatMap(metric => new Stream(Object.keys(metric.properties)))
        .distinct()
        .toList();

    return (
        <div id="monitoring-properties">
            {getContent()}
        </div>
    );

    function getContent() {
        return new Stream(properties)
            .map(property => <Property
                key={property}
                property={property}
                hiddenProperties={hiddenProperties}
                setHiddenProperties={setHiddenProperties}
            />)
            .toList();
    }
}

const Property = ({ property, hiddenProperties, setHiddenProperties }) => {
    return <PostLabeledInputField
        label={property}
        input={<InputField
            type="checkbox"
            className="monitoring-property"
            checked={!hiddenProperties.includes(property)}
            onchangeCallback={checked => checked ?
                removeAndSet(hiddenProperties, item => item == property, setHiddenProperties)
                :
                addAndSet(hiddenProperties, property, setHiddenProperties)
            }
        />
        }
    />
}

export default PropertyFilter;