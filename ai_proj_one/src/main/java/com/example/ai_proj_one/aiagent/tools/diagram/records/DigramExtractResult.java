package com.example.ai_proj_one.aiagent.tools.diagram.records;

import java.util.List;

public record DigramExtractResult(
    List<Node> nodes,
    List<Edge> edges,
    List<DataStore> dataStores,
    List<TrustBoundary> trustBoundaries
) {}
