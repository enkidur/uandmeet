package com.project.uandmeet.dto.boardDtoGroup;

import lombok.*;


public class LikeDto {

     @ToString
     @Getter
     @Setter
     @AllArgsConstructor
     @NoArgsConstructor
     public static class request {
          private String boardType;
          private Long boardId;
          private Boolean isLike;
     }

     @ToString
     @Getter
     @Setter
     @NoArgsConstructor
     @AllArgsConstructor
     public static class response {
          private Long likeCount;
     }
}
