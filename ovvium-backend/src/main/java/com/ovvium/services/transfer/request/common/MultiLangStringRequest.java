package com.ovvium.services.transfer.request.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MultiLangStringRequest {

    @NotBlank
    private String defaultValue;
    private Map<String, String> translations = emptyMap();
}
