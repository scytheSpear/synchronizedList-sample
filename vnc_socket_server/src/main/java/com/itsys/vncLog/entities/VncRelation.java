package com.itsys.vncLog.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VncRelation {

    private String clientIp;
    private String serverIp;
    private String startTime;
    private String endTime;
    private String id;
    
}
