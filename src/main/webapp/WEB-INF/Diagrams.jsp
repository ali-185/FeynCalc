<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="outputDiagrams">
  <c:forEach items="${diagramList}" var="diagram" varStatus="diagramLoop">
    <div class="outputDiagramContainer">
      <div id="diagram${diagramLoop.index + index}" class="outputDiagram">
      </div>
    </div>
  </c:forEach>
</div>
<script>
  <c:forEach items="${diagramList}" var="diagram" varStatus="diagramLoop">
  $("#diagram${diagramLoop.index + index}").autofeyn({
    incoming: loaded.incoming,
    outgoing: loaded.outgoing,
    vertex:   loaded.vertex,
    electron: ${diagram.electronConnections},
    positron: ${diagram.positronConnections},
    photon:   ${diagram.photonConnections},
  });
  </c:forEach>
  <c:if test="${index == 0 && empty diagramList}">
    $("#message").show();
  </c:if>
  <c:if test="${more}">
  	$("#more").show();
  </c:if>
</script>