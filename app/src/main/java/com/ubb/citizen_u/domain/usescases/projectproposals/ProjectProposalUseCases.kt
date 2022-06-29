package com.ubb.citizen_u.domain.usescases.projectproposals

data class ProjectProposalUseCases(
    val proposeProjectUseCase: ProposeProjectUseCase,
    val getCitizenProposedProjectsUseCase: GetCitizenProposedProjectsUseCase,
    val getOthersProposedProjectsUseCase: GetOthersProposedProjectsUseCase,
    val getProposedProjectUseCase: GetProposedProjectUseCase,
    val addCommentToProjectProposalUseCase: AddCommentToProjectProposalUseCase,
    val voteProjectProposalUseCaseUseCase: VoteProjectProposalUseCase,
    val undoVoteProjectProposalUseCaseUseCase: UndoVoteProjectProposalUseCase,
) {
}