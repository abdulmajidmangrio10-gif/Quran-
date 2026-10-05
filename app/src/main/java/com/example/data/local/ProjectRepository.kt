package com.example.data.local

import com.example.data.model.EditingProject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(private val dao: ProjectDao) {

    // Clean flow: strictly saved user projects, no dummy/sample pre-made projects
    val allProjects: Flow<List<EditingProject>> = dao.getAllProjects().map { entities ->
        entities.map { entity ->
            EditingProject(
                id = entity.id,
                title = entity.title,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                durationMs = entity.durationMs,
                videoUri = entity.videoUri,
                thumbnailUri = entity.thumbnailUri
            )
        }
    }

    suspend fun saveProject(project: EditingProject) {
        val entity = ProjectEntity(
            id = project.id,
            title = project.title,
            createdAt = project.createdAt,
            updatedAt = System.currentTimeMillis(),
            durationMs = project.durationMs,
            videoUri = project.videoUri,
            thumbnailUri = project.thumbnailUri,
            projectJson = ""
        )
        dao.insertProject(entity)
    }

    suspend fun deleteProject(id: String) {
        dao.deleteProject(id)
    }
}
