import gruppe from '../index'
import { sokSelectorOversikt } from '../index'

describe('gruppeReducer', () => {
	const initialState = {
		data: null,
		createOrUpdateId: null, // null = ingen, -1 = opprett ny gruppe, '45235' (ex: 425323) = rediger
		visning: 'mine'
	}

	it('should return gruppe with initialstate', () => {
		expect(gruppe(undefined, {})).toEqual(initialState)
	})

	it('should return initial state after LOCATION_CHANGE', () => {
		const action = {
			type: '@@router/LOCATION_CHANGE'
		}

		expect(gruppe({}, action)).toEqual(initialState)
	})

	it('should handle success getting a single group', () => {
		const testdata = 'test'
		const action = {
			type: 'GET_GRUPPE_SUCCESS',
			payload: { data: testdata }
		}

		const res = {
			data: [testdata]
		}

		expect(gruppe({}, action)).toEqual(res)
	})

	it('should handle success getting a array of groups', () => {
		const testdata = ['test1', 'test2']

		const action = {
			type: 'GET_GRUPPER_SUCCESS',
			payload: { data: testdata }
		}
		const res = {
			data: testdata
		}
		expect(gruppe({}, action)).toEqual(res)
	})

	it('should handle success updating a datarow', () => {
		const prevState = {
			createOrUpdateId: 1,
			data: [{ id: 1, value: 'test' }]
		}

		const newData = { id: 1, value: 'test_update' }

		const action = {
			type: 'UPDATE_GRUPPE_SUCCESS',
			payload: { data: newData }
		}

		const res = {
			createOrUpdateId: null,
			data: [newData]
		}

		expect(gruppe(prevState, action)).toEqual(res)
	})

	it('should handle success deleting a row', () => {
		const prevState = {
			data: [{ id: 1, value: 'test' }]
		}

		const action = {
			type: 'DELETE_GRUPPE_SUCCESS',
			meta: { gruppeId: 1 }
		}

		const res = {
			data: []
		}

		expect(gruppe(prevState, action)).toEqual(res)
	})

	it('should set a id for updating/creating group', () => {
		const testdata = 1

		const action = {
			type: 'TOGGLE_SHOW_CREATE_OR_EDIT',
			payload: testdata
		}

		const res = {
			createOrUpdateId: testdata
		}

		expect(gruppe({}, action)).toEqual(res)
	})

	it('should set create/update id to null', () => {
		const action = {
			type: 'CANCEL_CREATE_OR_EDIT'
		}

		const res = {
			createOrUpdateId: null
		}
		expect(gruppe({}, action)).toEqual(res)
	})

	it('should set visning', () => {
		const testdata = 'test'
		const action = {
			type: 'SETT_VISNING',
			payload: testdata
		}

		const res = {
			visning: testdata
		}

		expect(gruppe({}, action)).toEqual(res)
	})
})

describe('gruppeReducer-sokSelector', () => {
	const testdata = [{ id: 'a' }, { id: 'b' }]
	it('should return a filtered list', () => {
		const res = [{ id: 'a' }]
		expect(sokSelectorOversikt(testdata, 'a')).toEqual(res)
	})

	it('should return whole list', () => {
		expect(sokSelectorOversikt(testdata)).toEqual(testdata)
	})

	it('should return null when list is undefined', () => {
		expect(sokSelectorOversikt(null)).toEqual(null)
	})
})
