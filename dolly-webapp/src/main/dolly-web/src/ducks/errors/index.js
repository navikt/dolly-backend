export const createErrorMessageSelector = actions => state => {
	const errors = actions.map(action => state.errors[action])
	if (errors && errors[0]) {
		return errors[0]
	}
	return ''
}

export default function errorReducer(state = {}, action) {
	const { type, payload } = action
	const matches = /(.*)_(REQUEST|FAILURE)/.exec(type)

	// not a *_REQUEST / *_FAILURE actions, so we ignore them
	if (!matches) return state

	const [, requestName, requestState] = matches
	return {
		...state,
		// Store errorMessage
		// e.g. stores errorMessage when receiving GET_TODOS_FAILURE
		//      else clear errorMessage when receiving GET_TODOS_REQUEST
		[requestName]: requestState === 'FAILURE' ? payload.message : ''
	}
}
