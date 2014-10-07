package io.lumify.web.clientapi;

import io.lumify.web.clientapi.codegen.*;
import io.lumify.web.clientapi.model.User;
import io.lumify.web.clientapi.model.Workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LumifyApi {
    private final UserApi userApi;
    private final WorkspaceApiExt workspaceApi;
    private final AdminApiExt adminApi;
    private final VertexApiExt vertexApi;
    private final OntologyApiExt ontologyApi;
    private final String basePath;
    private User me;

    public LumifyApi(String basePath) {
        this.basePath = basePath;

        userApi = new UserApi();
        userApi.setBasePath(basePath);

        workspaceApi = new WorkspaceApiExt();
        workspaceApi.setBasePath(basePath);

        adminApi = new AdminApiExt();
        adminApi.setBasePath(basePath);

        vertexApi = new VertexApiExt();
        vertexApi.setBasePath(basePath);

        ontologyApi = new OntologyApiExt();
        ontologyApi.setBasePath(basePath);
    }

    public UserApi getUserApi() {
        return userApi;
    }

    public WorkspaceApiExt getWorkspaceApi() {
        return workspaceApi;
    }

    public AdminApiExt getAdminApi() {
        return adminApi;
    }

    public VertexApiExt getVertexApi() {
        return vertexApi;
    }

    public OntologyApiExt getOntologyApi() {
        return ontologyApi;
    }

    public String getCurrentWorkspaceId() {
        return ApiInvoker.getInstance().getWorkspaceId();
    }

    public Workspace loginAndGetCurrentWorkspace() throws ApiException {
        me = getUserApi().getMe();
        ApiInvoker.getInstance().setCsrfToken(me.getCsrfToken());

        List<Workspace> workspaces = getWorkspaceApi().getAll().getWorkspaces();

        Workspace currentWorkspace = null;
        if (me.getCurrentWorkspaceId() != null) {
            for (Workspace workspace : workspaces) {
                if (workspace.getWorkspaceId().equals(me.getCurrentWorkspaceId())) {
                    currentWorkspace = workspace;
                    break;
                }
            }
        }

        if (currentWorkspace == null) {
            if (workspaces.size() == 0) {
                currentWorkspace = getWorkspaceApi().create();
            } else {
                currentWorkspace = workspaces.get(0);
            }
        }

        ApiInvoker.getInstance().setWorkspaceId(currentWorkspace.getWorkspaceId());

        return currentWorkspace;
    }

    public String invokeAPI(String path, String method, Map<String, String> queryParams, Object body, Map<String, String> headerParams, Map<String, String> formParams, String contentType) throws ApiException {
        if (queryParams == null) {
            queryParams = new HashMap<String, String>();
        }
        if (headerParams == null) {
            headerParams = new HashMap<String, String>();
        }
        if (formParams == null) {
            formParams = new HashMap<String, String>();
        }
        return ApiInvoker.getInstance().invokeAPI(this.basePath, path, method, queryParams, body, headerParams, formParams, contentType);
    }

    public void logout() throws ApiException {
        invokeAPI("/logout", "POST", null, null, null, null, null);
    }

    public String getCurrentUserId() {
        return me.getId();
    }

    public void setWorkspaceId(String workspaceId) {
        ApiInvoker.getInstance().setWorkspaceId(workspaceId);
    }
}
