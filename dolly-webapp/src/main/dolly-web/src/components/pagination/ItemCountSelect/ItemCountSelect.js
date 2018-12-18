import React, { Fragment } from 'react'
import PropTypes from 'prop-types'
import Select from 'react-select'

const options = [
	{
		value: 10,
		label: 10
	},
	{
		value: 20,
		label: 20
	},
	{
		value: 50,
		label: 50
	}
]

export default function ItemCountSelect({ value, onChangeHandler }) {
	return (
		<div className="pagination-itemcount">
			Antall elementer i tabell
			<Select
				id="item-count"
				name="item-count"
				openOnFocus
				clearable={false}
				value={value}
				options={options}
				onChange={onChangeHandler}
			/>
		</div>
	)
}
