import Request from '../Request'
import Endpoints from './DollyEndpoints'
import Utils from './Utils'

export default class DollyService {
	// UTILS
	static Utils = Utils

	// Grupper
	static getGrupper() {
		return Request.get(Endpoints.gruppe())
	}

	static getGruppeById(gruppeId) {
		return Request.get(Endpoints.gruppeById(gruppeId))
	}

	static getGruppeByUserId(userId) {
		return Request.get(Endpoints.gruppeByUser(userId))
	}

	static getGruppeByTeamId(teamId) {
		return Request.get(Endpoints.gruppeByTeam(teamId))
	}

	static createGruppe(data) {
		return Request.post(Endpoints.gruppe(), data)
	}

	static updateGruppe(gruppeId, data) {
		return Request.put(Endpoints.gruppeById(gruppeId), data)
	}

	static deleteGruppe(gruppeId) {
		return Request.delete(Endpoints.gruppeById(gruppeId))
	}

	static updateGruppeAttributter(gruppeId, data) {
		return Request.put(Endpoints.gruppeAttributter(gruppeId), data)
	}

	static updateGruppeIdenter(gruppeId, data) {
		return Request.put(Endpoints.gruppeIdenter(gruppeId, data))
	}

	static createBestilling(gruppeId, data) {
		return Request.post(Endpoints.gruppeBestilling(gruppeId), data)
	}

	// Team
	static getTeams() {
		return Request.get(Endpoints.team())
	}

	static getTeamsByUserId(userId) {
		return Request.get(Endpoints.teamByUser(userId))
	}

	static getTeamById(teamId) {
		return Request.get(Endpoints.teamById(teamId))
	}

	static createTeam(data) {
		return Request.post(Endpoints.team(), data)
	}

	static updateTeam(teamId, data) {
		return Request.put(Endpoints.teamById(teamId), data)
	}

	static deleteTeam(teamId) {
		return Request.delete(Endpoints.teamById(teamId))
	}

	static addTeamMedlemmer(teamId, userArray) {
		return Request.put(Endpoints.teamAddMember(teamId), userArray)
	}

	static removeTeamMedlemmer(teamId, userArray) {
		return Request.put(Endpoints.teamRemoveMember(teamId), userArray)
	}

	// Bruker
	static getBrukere() {
		return Request.get(Endpoints.bruker())
	}

	static getBrukereById(brukerId) {
		return Request.get(Endpoints.brukerById())
	}

	static getCurrentBruker() {
		return Request.get(Endpoints.currentBruker())
	}

	static addFavorite(groupId) {
		return Request.put(Endpoints.addFavorite(), { gruppeId: groupId })
	}

	static removeFavorite(groupId) {
		return Request.put(Endpoints.removeFavorite(), { gruppeId: groupId })
	}

	// Kodeverk
	static getKodeverkByNavn(kodeverkNavn) {
		return Request.get(Endpoints.kodeverkByNavn(kodeverkNavn))
	}

	// Bestilling
	static getBestillinger(gruppeId) {
		return Request.get(Endpoints.bestillinger(gruppeId))
	}

	static getBestillingStatus(bestillingId) {
		return Request.get(Endpoints.bestillingStatus(bestillingId))
	}

	static getConfig() {
		return Request.get(Endpoints.config())
	}

	static postOpenAm(body) {
		return Request.post(Endpoints.openAm(), body)
	}

	static putOpenAmGroupStatus(groupId) {
		return Request.put(Endpoints.openAmGroupStatus(groupId))
	}

	static cancelBestilling(bestillingId) {
		return Request.delete(Endpoints.removeBestilling(bestillingId))
	}

	static deleteTestIdent(identId) {
		return Request.delete(Endpoints.removeTestIdent(identId))
	}

	//Oppslag
	static getEnhetByTknr (tknr) {
		return Request.get(Endpoints.enhetByTknr(tknr))
	}
}
