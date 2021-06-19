package com.github.saphyra.apphub.api.skyxplore.model.game;

import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SystemConnectionModel extends GameItem {
    private Line line;
}
