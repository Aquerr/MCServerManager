package pl.bartlomiejstepien.mcsm.domain.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class ServerId
{
    int serverId;
}
