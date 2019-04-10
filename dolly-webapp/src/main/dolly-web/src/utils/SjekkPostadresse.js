import { TpsfApi } from '~/service/Api'

const Adressesjekk = {}

Adressesjekk.sjekkPostadresse = async values => {
	let gyldigAdresse = true
	const regex = /^\d{4}(\s|$)/

	if (values['postLand'] === '' || values['postLand'] === 'NOR') {
		if (values['postLinje1'] && !values['postLinje2'] && !values['postLinje3']) {
			if (
				regex.test(!values['postLinje1']) ||
				(await Adressesjekk.sjekkPostnummer(values['postLinje1'].substring(0, 4))) === '08'
			) {
				gyldigAdresse = false
			}
		} else if (values['postLinje1'] && values['postLinje2'] && !values['postLinje3']) {
			if (
				regex.test(!values['postLinje2']) ||
				(await Adressesjekk.sjekkPostnummer(values['postLinje2'].substring(0, 4))) === '08'
			) {
				gyldigAdresse = false
			}
		} else if (values['postLinje1'] && values['postLinje2'] && values['postLinje3']) {
			if (
				regex.test(!values['postLinje3']) ||
				(await Adressesjekk.sjekkPostnummer(values['postLinje3'].substring(0, 4))) === '08'
			) {
				gyldigAdresse = false
			}
		} else {
			gyldigAdresse = false
		}
	}
	return gyldigAdresse
}

Adressesjekk.sjekkPostnummer = async postnummer => {
	let respons = await TpsfApi.checkPostnummer(postnummer)
	const status = respons.data.response.status.kode
	return status
}

export default Adressesjekk
