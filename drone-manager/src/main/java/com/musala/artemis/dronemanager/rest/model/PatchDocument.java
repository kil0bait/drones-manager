package com.musala.artemis.dronemanager.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PatchDocument {
    @Schema(description = "The operation to be performed", requiredMode = Schema.RequiredMode.REQUIRED)
    private PatchOperation op;
    @Schema(description = "A JSON-Pointer", example = "/path/to/resource/1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String path;
    @Schema(description = "The value to be used within the operations", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = """
                    {
                        "anyJson": "value1"
                    }
                    """)
    private Object value;

    @Getter
    public enum PatchOperation {
        ADD("add"),
        REMOVE("remove"),
        REPLACE("replace"),
        MOVE("move"),
        COPY("copy"),
        TEST("test");

        private final String value;

        PatchOperation(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
