package com.nextlabs.destiny.console.dto.comparable;

import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.model.TagLabel;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

public class ComparableTag
        implements Comparable<ComparableTag> {

    private final TagType type;
    private final String key;
    private final String label;

    public ComparableTag(TagLabel tagLabel) {
        super();
        type = tagLabel.getType();
        key = tagLabel.getKey();
        label = tagLabel.getLabel();
    }

    public ComparableTag(TagDTO tagDTO) {
        super();
        type = TagType.getType(tagDTO.getType());
        key = tagDTO.getKey();
        label = tagDTO.getLabel();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ComparableTag)) return false;

        ComparableTag that = (ComparableTag) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .append(key, that.key)
                .append(label, that.label)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, key, label);
    }

    @Override
    public int compareTo(ComparableTag comparableTag) {
        if(type != null && !type.equals(comparableTag.type)) {
            return type.compareTo(comparableTag.type);
        }
        if(key != null && !key.equals(comparableTag.key)) {
            return key.compareTo(comparableTag.key);
        }
        if(label != null && !label.equals(comparableTag.label)) {
            return label.compareTo(comparableTag.label);
        }

        return 0;
    }
}
