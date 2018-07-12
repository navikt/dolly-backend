import { DollyApi } from '~/service/Api'

export const types = {
	REQUEST_TEAMS: 'team/REQUEST_TEAMS',
	REQUEST_TEAMS_SUCCESS: 'team/REQUEST_TEAM_SUCCESS',
	REQUEST_TEAMS_ERROR: 'team/REQUEST_TEAMS_ERROR',
	SET_TEAM_VISNING: 'team/SET_TEAM_VISNING',
	CREATE_TEAM_REQUEST: 'team/CREATE_TEAM_REQUEST',
	CREATE_TEAM_SUCCESS: 'team/CREATE_TEAM_SUCCESS',
	CREATE_TEAM_ERROR: 'team/CREATE_TEAM_ERROR',
	UPDATE_TEAM_REQUEST: 'team/UPDATE_TEAM_REQUEST',
	UPDATE_TEAM_SUCCESS: 'team/UPDATE_TEAM_SUCCESS',
	UPDATE_TEAM_ERROR: 'team/UPDATE_TEAM_ERROR',
	START_OPPRETT_TEAM: 'team/START_OPPRETT_TEAM',
	START_REDIGER_TEAM: 'team/START_REDIGER_TEAM',
	CLOSE_OPPRETT_REDIGER_TEAM: 'team/CLOSE_OPPRETT_REDIGER_TEAM'
}

const initialState = {
	fetching: false,
	items: [],
	visning: 'mine',
	visOpprettTeam: false,
	editTeamId: null
}

export default function teamReducer(state = initialState, action) {
	switch (action.type) {
		case types.REQUEST_TEAMS:
			return {
				...state,
				fetching: true
			}
		case types.REQUEST_TEAMS_SUCCESS:
			return {
				...state,
				fetching: false,
				items: action.teams
			}
		case types.REQUEST_TEAMS_ERROR:
			return {
				...state,
				fetching: false,
				error: action.error
			}
		case types.CREATE_TEAM_REQUEST:
			return {
				...state,
				fetching: true
			}
		case types.CREATE_TEAM_SUCCESS:
			return {
				...state,
				fetching: false,
				items: [...state.items, action.team]
			}
		case types.CREATE_TEAM_ERROR:
			return {
				...state,
				fetching: false,
				error: action.error
			}
		case types.UPDATE_TEAM_REQUEST:
			return {
				...state,
				fetching: true
			}
		case types.UPDATE_TEAM_SUCCESS:
			return {
				...state,
				fetching: false,
				items: items.map(item => {
					if (item.id !== action.team.id) return item

					return {
						...item,
						...action.team
					}
				})
			}
		case types.UPDATE_TEAM_ERROR:
			return {
				...state,
				fetching: false,
				error: action.error
			}
		case types.SET_TEAM_VISNING:
			return {
				...state,
				visning: action.visning
			}
		case types.START_OPPRETT_TEAM:
			return {
				...state,
				visOpprettTeam: true,
				editTeamId: null
			}
		case types.START_REDIGER_TEAM:
			return {
				...state,
				visOpprettTeam: false,
				editTeamId: action.teamId
			}
		case types.CLOSE_OPPRETT_REDIGER_TEAM:
			return {
				...state,
				visOpprettTeam: false,
				editTeamId: null
			}
		default:
			return state
	}
}

const requestTeams = () => ({
	type: types.REQUEST_TEAMS
})

const requestTeamsSuccess = teams => ({
	type: types.REQUEST_TEAMS_SUCCESS,
	teams
})

const requestTeamsError = error => ({
	type: types.REQUEST_TEAMS_ERROR
})

const createTeamRequest = () => ({
	type: types.CREATE_TEAM_REQUEST
})

const createTeamSuccess = team => ({
	type: types.CREATE_TEAM_SUCCESS,
	team
})

const createTeamError = error => ({
	type: types.CREATE_TEAM_ERROR,
	error
})

const updateTeamRequest = () => ({
	type: types.UPDATE_TEAM_REQUEST
})

const updateTeamSuccess = team => ({
	type: types.UPDATE_TEAM_SUCCESS,
	team
})

const updateTeamError = error => ({
	type: types.UPDATE_TEAM_ERROR,
	error
})

export const setTeamVisning = visning => ({ type: types.SET_TEAM_VISNING, visning })
export const startOpprettTeam = () => ({ type: types.START_OPPRETT_TEAM })
export const startRedigerTeam = teamId => ({ type: types.START_REDIGER_TEAM, teamId })
export const closeOpprettRedigerTeam = () => ({ type: types.CLOSE_OPPRETT_REDIGER_TEAM })

// THUNKS
export const fetchTeams = () => async (dispatch, getState) => {
	const { bruker, team } = getState()
	const currentBrukerId = bruker.brukerData.navIdent
	const currentVisning = team.visning
	try {
		dispatch(requestTeams())

		const response =
			currentVisning === 'mine'
				? await DollyApi.getTeamsByUserId(currentBrukerId)
				: await DollyApi.getTeams()

		return dispatch(requestTeamsSuccess(response.data))
	} catch (error) {
		dispatch(requestTeamsError)
	}
}

export const createTeam = data => async dispatch => {
	try {
		dispatch(createTeamRequest())
		const response = await DollyApi.createTeam(data)
		dispatch(createTeamSuccess(response.data))
	} catch (error) {
		dispatch(createTeamError(error))
	}
}

export const updateTeam = (teamId, data) => async dispatch => {
	try {
		dispatch(updateTeamRequest())
		const response = await DollyApi.updateTeam(teamId, data)
		dispatch(updateTeamSuccess(response.data))
	} catch (error) {
		dispatch(updateTeamError(error))
	}
}
