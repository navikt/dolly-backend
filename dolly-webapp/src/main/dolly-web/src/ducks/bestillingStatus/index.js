import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import Formatters from '~/utils/DataFormatter'
import success from '~/utils/SuccessAction'
import { actions as bestillingActions } from '~/ducks/bestilling'

export const getBestillinger = createAction('GET_BESTILLINGER', async gruppeID => {
	let res = await DollyApi.getBestillinger(gruppeID)
	return res
})

export const removeNyBestillingStatus = createAction('REMOVE_NY_BESTILLING_STATUS')

// ny-array holder oversikt over nye bestillinger i en session
const initialState = { ny: [] }

export const cancelBestilling = createAction('CANCEL_BESTILLING', async id => {
	let res = await DollyApi.cancelBestilling(id)
	return res
})

export const gjenopprettBestilling = createAction('GJENOPPRETT_BESTILLING', async (id, envs) => {
	let res = await DollyApi.gjenopprettBestilling(id, envs)
	return res
})

export default handleActions(
	{
		[success(getBestillinger)](state, action) {
			const { data } = action.payload
			const nyeBestillinger = data.filter(bestilling => {
				if (!bestilling.ferdig) return true
			})
			let idListe = []
			nyeBestillinger.forEach(bestilling => {
				if (!state.ny.find(id => id == bestilling.id)) idListe.push(bestilling.id)
			})
			return {
				...state,
				data,
				ny: idListe.length > 0 ? [...state.ny, ...idListe] : state.ny
			}
		},

		// [success(bestillingActions.postBestilling)](state, action) {
		// 	return { ...state, ny: [...state.ny, action.payload.data.id] }
		// },

		// [success(gjenopprettBestilling)](state, action) {
		// 	return { ...state, ny: [...state.ny, action.payload.data.id] }
		// },

		// [success(cancelBestilling)](state, action) {
		// 	return { ...state, ny: state.ny.filter(id => id !== action.payload.id) }
		// }

		[removeNyBestillingStatus](state, action) {
			return { ...state, ny: state.ny.filter(id => id !== action.payload) }
		}
	},
	initialState
)

// Selector + mapper
export const sokSelector = (items, searchStr) => {
	if (!items) return null
	const mappedItems = mapItems(items)

	if (!searchStr) return mappedItems

	const query = searchStr.toLowerCase()
	return mappedItems.filter(item => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'antallIdenter'),
			_get(item, 'sistOppdatert'),
			_get(item, 'environments'),
			_get(item, 'ferdig')
		]
			.filter(v => !_isNil(v))
			.map(v => v.toString().toLowerCase())

		return searchValues.some(v => v.includes(query))
	})
}

// Selector
export const miljoStatusSelector = bestilling => {
	if (!bestilling) return null

	const id = bestilling.id
	let successEnvs = []
	let failedEnvs = []
	const finnesFeilmelding = avvikStatus(bestilling)
	const antallIdenterOpprettet = antallIdenterOpprettetFunk(bestilling)

	//Finn feilet og suksess miljø
	bestilling.krrStubStatus &&
		bestilling.krrStubStatus.map(status => {
			status.statusMelding == 'OK'
				? !successEnvs.includes('Krr-stub') && successEnvs.push('Krr-stub')
				: !failedEnvs.includes('Krr-stub') && failedEnvs.push('Krr-stub')
		})
	bestilling.sigrunStubStatus &&
		bestilling.sigrunStubStatus.map(status => {
			if (status.statusMelding == 'OK') {
				!successEnvs.includes('Sigrun-stub') && successEnvs.push('Sigrun-stub')
			} else {
				!failedEnvs.includes('Sigrun-stub') && failedEnvs.push('Sigrun-stub')
			}
		})
	bestilling.tpsfStatus &&
		bestilling.tpsfStatus.map(status => {
			status.statusMelding !== 'OK' &&
				Object.keys(status.environmentIdents).map(miljo => {
					const lowMiljo = miljo.toLowerCase()
					!failedEnvs.includes(lowMiljo) && failedEnvs.push(lowMiljo)
				})
		})
	//Går gjennom TPSF-statuser igjen slik at ingen miljø er både suksess og feilet
	bestilling.tpsfStatus &&
		bestilling.tpsfStatus.map(status => {
			status.statusMelding == 'OK' &&
				Object.keys(status.environmentIdents).map(miljo => {
					const lowMiljo = miljo.toLowerCase()
					!failedEnvs.includes(lowMiljo) &&
						(!successEnvs.includes(lowMiljo) && successEnvs.push(lowMiljo))
				})
		})

	//TODO: Hvis bestilling failer 100 % fra TPSF finnes ikke støtte for retry.

	return { id, successEnvs, failedEnvs, bestilling, finnesFeilmelding, antallIdenterOpprettet }
}

const antallIdenterOpprettetFunk = bestilling => {
	let identArray = []
	bestilling.tpsfStatus &&
		bestilling.tpsfStatus.map(status => {
			Object.keys(status.environmentIdents).map(miljo => {
				status.environmentIdents[miljo].map(ident => {
					!identArray.includes(ident) && identArray.push(ident)
				})
			})
		})
	return identArray.length
}

const mapItems = items => {
	if (!items) return null
	return items.map(item => {
		return {
			...item,
			id: item.id.toString(),
			antallIdenter: item.antallIdenter.toString(),
			sistOppdatert: Formatters.formatDate(item.sistOppdatert),
			ferdig: item.stoppet
				? 'Stoppet'
				: bestillingIkkeFerdig(item)
					? 'Pågår'
					: harIkkeIdenter(item)
						? 'Feilet'
						: avvikStatus(item)
							? 'Avvik'
							: 'Ferdig'
		}
	})
}

const avvikStatus = item => {
	let avvik = false
	item.tpsfStatus &&
		item.tpsfStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.krrStubStatus &&
		item.krrStubStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.sigrunStubStatus &&
		item.sigrunStubStatus.map(status => {
			status.statusMelding !== 'OK' && (avvik = true)
		})
	item.feil && (avvik = true)
	return avvik
}

const bestillingIkkeFerdig = item => !item.ferdig

const harIkkeIdenter = item => {
	let feilet = true
	item.tpsfStatus && (feilet = false)
	return feilet
}
