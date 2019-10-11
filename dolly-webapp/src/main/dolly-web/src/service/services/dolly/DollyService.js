import Request from '~/service/services/Request'
import Endpoints from './DollyEndpoints'
import Utils from './Utils'

export default {
	// UTILS
	Utils,

	// Grupper
	getGrupper() {
		return Request.get(Endpoints.gruppe())
	},

	getGruppeById(gruppeId) {
		return Request.get(Endpoints.gruppeById(gruppeId))
	},

	getGruppeByUserId(userId) {
		return Request.get(Endpoints.gruppeByUser(userId))
	},

	getGruppeByTeamId(teamId) {
		return Request.get(Endpoints.gruppeByTeam(teamId))
	},

	createGruppe(data) {
		return Request.post(Endpoints.gruppe(), data)
	},

	updateGruppe(gruppeId, data) {
		return Request.put(Endpoints.gruppeById(gruppeId), data)
	},

	deleteGruppe(gruppeId) {
		return Request.delete(Endpoints.gruppeById(gruppeId))
	},

	updateGruppeAttributter(gruppeId, data) {
		return Request.put(Endpoints.gruppeAttributter(gruppeId), data)
	},

	createBestilling(gruppeId, data) {
		return Request.post(Endpoints.gruppeBestilling(gruppeId), data)
	},

	createBestillingFraEksisterendeIdenter(gruppeId, data) {
		return Request.post(Endpoints.gruppeBestillingFraEksisterendeIdenter(gruppeId), data)
	},

	// Team
	getTeams() {
		return Request.get(Endpoints.team())
	},

	getTeamsByUserId(userId) {
		return Request.get(Endpoints.teamByUser(userId))
	},

	getTeamById(teamId) {
		return Request.get(Endpoints.teamById(teamId))
	},

	createTeam(data) {
		return Request.post(Endpoints.team(), data)
	},

	updateTeam(teamId, data) {
		return Request.put(Endpoints.teamById(teamId), data)
	},

	deleteTeam(teamId) {
		return Request.delete(Endpoints.teamById(teamId))
	},

	addTeamMedlemmer(teamId, userArray) {
		return Request.put(Endpoints.teamAddMember(teamId), userArray)
	},

	removeTeamMedlemmer(teamId, user) {
		return Request.delete(Endpoints.teamRemoveMember(teamId, user))
	},

	// Bruker
	getBrukere() {
		return Request.get(Endpoints.bruker())
	},

	getBrukereById(brukerId) {
		return Request.get(Endpoints.brukerById())
	},

	getCurrentBruker() {
		return Request.get(Endpoints.currentBruker())
	},

	addFavorite(groupId) {
		return Request.put(Endpoints.addFavorite(), { gruppeId: groupId })
	},

	removeFavorite(groupId) {
		return Request.put(Endpoints.removeFavorite(), { gruppeId: groupId })
	},

	//* Kodeverk
	getKodeverkByNavn(kodeverkNavn) {
		return Request.get(Endpoints.kodeverkByNavn(kodeverkNavn))
	},

	//* Bestilling
	getBestillinger(gruppeId) {
		return Request.get(Endpoints.bestillinger(gruppeId))
	},

	getBestillingMaler() {
		return Request.get(Endpoints.bestillingMal())
	},

	getBestillingStatus(bestillingId) {
		return Request.get(Endpoints.bestillingStatus(bestillingId))
	},

	gjenopprettBestilling(bestillingId, envs) {
		return Request.post(Endpoints.gjenopprettBestilling(bestillingId, envs))
	},

	getConfig() {
		return Request.get(Endpoints.config())
	},

	postOpenAmBestilling(bestillingId) {
		return Request.post(Endpoints.openAmBestilling(bestillingId))
	},

	cancelBestilling(bestillingId) {
		return Request.delete(Endpoints.removeBestilling(bestillingId))
	},

	deleteTestIdent(gruppeId, identId) {
		return Request.delete(Endpoints.removeTestIdent(gruppeId, identId))
	},

	//Oppslag
	getEnhetByTknr(tknr) {
		return Request.get(Endpoints.enhetByTknr(tknr))
	},

	getPersonFraPersonoppslag(ident) {
		return Request.get(Endpoints.personoppslag(ident))
	},

	//AAREG
	getArbeidsforhold(ident, env) {
		return Request.get(Endpoints.arbeidsforholdByIdent(ident, env))
	}
}
