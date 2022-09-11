package com.project.uandmeet.dto.boardDtoGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
     private String boardType;
     private Long boardId;
     private Boolean isLike;
}
