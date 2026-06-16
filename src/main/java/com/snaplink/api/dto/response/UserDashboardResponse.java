package com.snaplink.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardResponse {

    private long totalLinksCreated;
    private long totalClicksAccumulated;
}
