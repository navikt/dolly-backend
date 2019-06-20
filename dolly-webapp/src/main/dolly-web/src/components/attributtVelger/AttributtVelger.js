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

	render() {
		const { selectedIds, uncheckAllAttributes } = this.props

		return (
			<div className="attributt-velger">
				<Input
					label="Søk attributter"
					labelOffscreen
					placeholder="Søk etter egenskaper"
					className="attributt-velger_search"
					onChange={this._searchOnChange}
				/>
				<div className="flexbox">
					<div className="attributt-velger_panels">{this._renderPanels()}</div>
					<Utvalg selectedIds={selectedIds} uncheckAllAttributes={uncheckAllAttributes} />
				</div>
			</div>
		)
	}

	_searchOnChange = e => this.setState({ search: e.target.value })

	_renderPanels = () => {
		const { currentBestilling } = this.props
		const list = this.AttributtManager.listSelectableAttributes(
			this.state.search,
			currentBestilling.identOpprettesFra
		)
		if (list.length === 0) return this._renderEmptyResult()
		return list.map(hovedKategori => this._renderHovedKategori(hovedKategori))
	}

	_renderHovedKategori = ({ hovedKategori, items }) => {
		const { uncheckAttributeArray, checkAttributeArray } = this.props
		const name = hovedKategori.navn
		const hovedKategoriItems = this.AttributtManager.getParentAttributtListByHovedkategori(
			hovedKategori
		)
		return (
			<Panel
				key={name}
				heading={<h2>{name}</h2>}
				startOpen={false}
				checkAttributeArray={() => checkAttributeArray(hovedKategoriItems)}
				uncheckAttributeArray={() => uncheckAttributeArray(hovedKategoriItems)}
				informasjonstekst={hovedKategori.informasjonstekst}
			>
				<fieldset name={name}>
					<div className="attributt-velger_panelcontent">
						{items.map((subKategori, idx) => this._renderSubKategori(subKategori, idx))}
					</div>
				</fieldset>
			</Panel>
		)
	}

	_renderSubKategori = ({ subKategori, items }, idx) => {
		return (
			<Fragment key={idx}>
				{subKategori && subKategori.navn != '' && <h3>{subKategori.navn}</h3>}
				<fieldset name={subKategori.navn}>
					<div className="attributt-velger_panelsubcontent">
						{items.map(item => this._renderItem(item))}
					</div>
				</fieldset>
			</Fragment>
		)
	}

	_renderItem = item => {
		const { attributeIds } = this.props.currentBestilling
		// *Dependency system, finner ut om attributtene kan toggles
		const disabled = item.dependentOn
			? !attributeIds.includes(item.dependentOn)
				? true
				: false
			: false

		const dependentBy = item.dependentBy ? item.dependentBy : null

		if (item.id === 'ufb_kommunenr' || item.id === 'utenFastBopel') return

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

	_renderEmptyResult = () => <p>Søket ga ingen treff</p>
}
