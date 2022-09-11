package com.project.uandmeet.dto.boardDtoGroup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StateCheckDto {

    private String boardType;
    private Boolean likeState;
    private Boolean matchingState;

    public StateCheckDto(String boardType, Boolean matchingState, Boolean likeState) {
        this.boardType =boardType;
        this.matchingState =matchingState;
        this.likeState = likeState;
    }
}
