import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Panel from '~/components/panel/Panel'
import Input from '~/components/fields/Input/Input'
import Utvalg from './Utvalg/Utvalg'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import { AttributtManager } from '~/service/Kodeverk'
import './AttributtVelger.less'
export default class AttributtVelger extends Component {
	static propTypes = {
		onToggle: PropTypes.func.isRequired,
		selectedIds: PropTypes.arrayOf(PropTypes.string)
	}

	constructor(props) {
		super(props)
		this.AttributtManager = new AttributtManager()
	}

	state = {
		search: ''
	}

	searchOnChange = e => this.setState({ search: e.target.value })

	renderPanels = () => {
		const list = this.AttributtManager.listSelectableAttributes(this.state.search)
		if (list.length === 0) return this.renderEmptyResult()
		return list.map(hovedKategori => this.renderHovedKategori(hovedKategori))
	}

	renderHovedKategori = ({ hovedKategori, items }) => {
		const { uncheckAttributeArray, checkAttributeArray } = this.props
		const name = hovedKategori.navn
		const hovedKategoriItems = this.AttributtManager.getParentAttributtListByHovedkategori(
			hovedKategori
		)
		return (
			<Panel
				key={name}
				heading={<h2>{name}</h2>}
				startOpen
				checkAttributeArray={() => checkAttributeArray(hovedKategoriItems)}
				uncheckAttributeArray={() => uncheckAttributeArray(hovedKategoriItems)}
				informasjonstekst={hovedKategori.informasjonstekst}
			>
				<fieldset name={name}>
					<div className="attributt-velger_panelcontent">
						{items.map((subKategori, idx) => this.renderSubKategori(subKategori, idx))}
					</div>
				</fieldset>
			</Panel>
		)
	}

	renderSubKategori = ({ subKategori, items }, idx) => {
		return (
			<Fragment key={idx}>
				{subKategori && subKategori.navn != '' && <h3>{subKategori.navn}</h3>}
				<fieldset name={subKategori.navn}>
					<div className="attributt-velger_panelsubcontent">
						{items.map(item => this.renderItem(item))}
					</div>
				</fieldset>
			</Fragment>
		)
	}

	renderItem = item => {
		const { attributeIds } = this.props.currentBestilling

		// Dependency system, finner ut om attributtene kan toggles
		const disabled = item.dependentOn
			? !attributeIds.includes(item.dependentOn)
				? true
				: false
			: false

		const dependentBy = item.dependentBy ? item.dependentBy : null

		return (
			<Checkbox
				key={item.id}
				label={item.label}
				id={item.id}
				disabled={disabled}
				checked={this.props.selectedIds.includes(item.id)}
				onChange={
					dependentBy
						? e =>
								this._onToggleMultipleItems(
									e.target.id,
									dependentBy,
									this.props.selectedIds.includes(dependentBy)
								)
						: e => this.props.onToggle(e.target.id)
				}
			/>
		)
	}

	// Når man toggler av en attributt som er avhengig av en annen, må også dependentBy-attributten toggles av
	_onToggleMultipleItems = (dependentOn, dependentBy, isChecked) => {
		this.props.onToggle(dependentOn)
		isChecked && this.props.onToggle(dependentBy)
	}

	renderEmptyResult = () => <p>Søket ga ingen treff</p>

	render() {
		const { selectedIds, uncheckAllAttributes } = this.props

		return (
			<div className="attributt-velger">
				<Input
					label="Søk attributter"
					labelOffscreen
					placeholder="Søk etter egenskaper"
					className="attributt-velger_search"
					onChange={this.searchOnChange}
				/>
				<div className="flexbox">
					<div className="attributt-velger_panels">{this.renderPanels()}</div>
					<Utvalg selectedIds={selectedIds} uncheckAllAttributes={uncheckAllAttributes} />
				</div>
			</div>
		)
	}
}
