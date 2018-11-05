import { TpsfApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const getEnvironments = createAction('GET_ENVIRONMENTS', () =>
	TpsfApi.getTilgjengligeMiljoer().then(res => {
		return _getEnvironmentsSortedByType(res.data.environments)
	})
)

const initialState = {
	data: null
}

export default handleActions(
	{
		[success(getEnvironments)](state, action) {
			return { ...state, data: action.payload }
		}
	},
	initialState
)

export const _getEnvironmentsSortedByType = envArray => {
	let sortedByType = envArray.reduce((prev, curr) => {
		const label = curr.toUpperCase()
		const envType = label.charAt(0)
		if (prev[envType]) {
			prev[envType].push({ id: curr, label })
		} else {
			prev[envType] = [{ id: curr, label }]
		}
		return prev
	}, {})

	Object.keys(sortedByType).map(key => {
		const envs = sortedByType[key]
		sortedByType[key] = envs.sort((a, b) => {
			return a.label.substring(1) - b.label.substring(1)
		})
	})
	return sortedByType
}
