import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getBaseUrl = () => ConfigService.getDatesourceUrl('tpsf') || config.services.tpsfUrl
const getTpsfUrl = () => `${getBaseUrl()}/api/v1`

export default {
	getTestbrukere(userArray) {
		if (!userArray) return
		const endpoint = getTpsfUrl() + '/dolly/testdata/hentpersoner'

		// Må bruke post-request pga maxString-limit på en GET-request
		return Request.post(endpoint, userArray)
	},

	updateTestbruker(userData) {
		if (!userData) return
		const endpoint = getTpsfUrl() + '/testdata/updatepersoner'
		return Request.post(endpoint, [userData])
	},

	checkpersoner(userArray) {
		if (!userArray) return
		const endpoint = getTpsfUrl() + '/dolly/testdata/checkpersoner'
		return Request.post(endpoint, userArray)
	},

	sendToTps(data) {
		if (!data) return
		const endpoint = getTpsfUrl() + '/dolly/testdata/tilTpsFlere'
		return Request.post(endpoint, data)
	},

	createFoedselsmelding(userData) {
		const endpoint = getTpsfUrl() + '/tpsmelding/foedselsmelding'
		return Request.post(endpoint, userData)
	},

	getKontaktInformasjon(fnr, env) {
		const endpoint = getBaseUrl() + '/api/tps/kontaktinformasjon?fnr=' + fnr + '&environment=' + env
		return Request.get(endpoint)
	},

	createDoedsmelding(userData) {
		const endpoint = getTpsfUrl() + '/tpsmelding/doedsmelding'
		return Request.post(endpoint, userData)
	},

	getMiljoerByFnr(fnr) {
		const endpoint = getTpsfUrl() + '/testdata/tpsStatus?identer=' + fnr
		return Request.get(endpoint)
	},

	generateRandomAddress() {
		const endpoint = `${getTpsfUrl()}/gyldigadresse/tilfeldig?maxAntall=5`
		return Request.get(endpoint)
	},

	generateAddress(query) {
		const endpoint = `${getTpsfUrl()}/gyldigadresse/autocomplete?maxRetur=5${query}`
		return Request.get(endpoint)
	},

	autocompleteAddress(adresseSok) {
		const endpoint = getTpsfUrl() + '/gyldigadresse/autocomplete?adresseNavnsok=' + adresseSok
		return Request.get(endpoint)
	},

	checkPostnummer(postnummer) {
		const endpoint = getTpsfUrl() + '/gyldigadresse/autocomplete?postNrsok=' + postnummer
		return Request.get(endpoint)
	},

	getTilgjengligeMiljoer() {
		const endpoint = `${getTpsfUrl()}/environments`
		return Request.get(endpoint)
	},

	getExcelForIdenter(userArray) {
		if (!userArray) return
		const endpoint = `${getTpsfUrl()}/dolly/testdata/excel`
		return Request.post(endpoint, userArray)
	}
}
