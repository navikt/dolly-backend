import React, { Component, Fragment } from 'react'
import Knapp from 'nav-frontend-knapper'
import DollySelect from '~/components/fields/Select/Select'
import Input from '~/components/fields/Input/Input'
import { TpsfApi, DollyApi } from '~/service/Api'
import './AutofillAddress.less'

const initialState = {
	isFetching: false,
	// input: '',
	// adresserHentet: false,
	boadresse_gateadresse: '',
	boadresse_husnummer: '',
	boadresse_postnr: '',
	boadresse_kommunenr: ''
}

export default class AutofillAddress extends Component {
	state = { ...initialState }

	static gyldigeAdresser = []

	onInputChangeHandler = input => {
		this.setState({ ...this.state, [input.id]: input.value })
	}

	onGatenavnChangeHandler = input => {
		this.setState({ boadresse_gateadresse: input.target.value })
	}

	onHusnummerChangeHandler = input => {
		this.setState({ boadresse_husnummer: input.target.value })
	}

	placeHolderGenerator = type => {
		if (type === 'boadresse_postnr') return 'Velg postnummer...'
		if (type === 'boadresse_kommunenr') return 'Velg kommunenummer...'
		if (type === 'boadresse_gateadresse') return 'Søk etter adresse...'
		if (type === 'boadresse_husnummer') return 'Velg husnummer...'
		return ''
	}

	loadOptionsSelector = type => {
		if (type === 'boadresse_postnr') return () => this.fetchKodeverk('Postnummer', type)
		if (type === 'boadresse_kommunenr') return () => this.fetchKodeverk('Kommuner', type)
		// if (type === 'boadresse_gateadresse') rseturn () => this.fetchAdresser()
		return undefined
	}

	fetchKodeverk = (kodeverkNavn, type) => {
		return DollyApi.getKodeverkByNavn(kodeverkNavn).then(res => {
			return {
				options: res.data.koder.map(kode => ({
					label: `${kode.value} - ${kode.label}`,
					value: kode.value,
					id: type
				}))
			}
		})
	}

	fetchAdresser = () => {
		const adr = this.gyldigeAdresser
		var options = []
		if (adr.length > 1) {
			adr.forEach(adresse => {
				console.log('adresse :', adresse)
				options.push({
					label: adresse.adrnavn + ', ' + adresse.pnr + ' ' + adresse.psted,
					value: adresse.adrnavn,
					adrObject: adresse
				})
			})
		} else {
			options.push({
				label: adr.adrnavn + ', ' + adr.pnr + ' ' + adr.psted,
				value: adr.adrnavn,
				adrObject: adr
			})
		}
		return Promise.resolve({ options: options })
	}

	onAdresseChangeHandler = input => {
		const { formikProps } = this.props
		const addressData = input.adrObject

		let newAddressObject = {
			boadresse_gateadresse: '',
			boadresse_husnummer: '',
			boadresse_gatekode: '',
			boadresse_kommunenr: '',
			boadresse_postnr: ''
		}
		if (addressData) {
			const { adrnavn, husnrfra, gkode, pnr, knr } = addressData

			newAddressObject = {
				boadresse_gateadresse: adrnavn.toString(),
				boadresse_husnummer: husnrfra.toString(),
				boadresse_gatekode: gkode.toString(),
				boadresse_kommunenr: knr.toString(),
				boadresse_postnr: pnr.toString()
			}
			console.log('newAddressObject :', newAddressObject)
		}

		formikProps.setValues({ ...formikProps.values, ...newAddressObject })

		// console.log('input :', input)
		// return <p>test</p>
	}

	setQueryString = () => {
		let queryString = ''
		if (this.state.boadresse_gateadresse) {
			queryString += `&adresseNavnsok=${this.state.boadresse_gateadresse}`
		}
		if (this.state.boadresse_husnummer) {
			queryString += `&husNrsok=${this.state.boadresse_husnummer}`
		}
		if (this.state.boadresse_postnr) {
			queryString += `&postNrsok=${this.state.boadresse_postnr}`
		}
		if (this.state.boadresse_kommunenr) {
			queryString += `&kommuneNrsok=${this.state.boadresse_kommunenr}`
		}
		return queryString
	}

	onClickHandler = () => {
		return this.setState({ isFetching: true }, async () => {
			try {
				const query = this.setQueryString()
				let generateAddressResponse
				query.length > 0
					? (generateAddressResponse = await TpsfApi.generateAddress(query))
					: (generateAddressResponse = await TpsfApi.generateRandomAddress())

				const addressData = generateAddressResponse.data.response.data1.adrData
				console.log('addressData :', addressData)
				this.gyldigeAdresser = addressData

				const count = parseInt(generateAddressResponse.data.response.data1.antallForekomster)

				let status = generateAddressResponse.data.response.status.utfyllendeMelding

				return this.setState({ isFetching: false, adresserHentet: true, status })
			} catch (err) {
				return this.setState({ isFetching: false })
			}
		})
	}

	renderAdresseSelect = () => {
		return (
			<DollySelect
				placeholder="Velg gyldig adresse..."
				name="generator-select"
				label=""
				onChange={this.onAdresseChangeHandler}
				loadOptions={this.fetchAdresser}
			/>
		)
	}

	renderSelect = item => {
		const input = this.state
		const selectProps = {
			loadOptions: this.loadOptionsSelector(item.id),
			placeholder: this.placeHolderGenerator(item.id)
		}

		if (item.id === 'boadresse_gateadresse') {
			return (
				<Input
					key={item.id}
					label={item.label}
					placeholder={selectProps.placeholder}
					onChange={this.onGatenavnChangeHandler}
				/>
			)
		} else if (item.id === 'boadresse_husnummer') {
			return (
				<Input
					key={item.id}
					label={item.label}
					placeholder={selectProps.placeholder}
					onChange={this.onHusnummerChangeHandler}
				/>
			)
		}
		return (
			<DollySelect
				key={item.id}
				name="generator-select"
				label={item.label}
				onChange={this.onInputChangeHandler}
				value={input}
				{...selectProps}
			/>
		)
	}

	render() {
		const items = this.props.items
		console.log('this.state :', this.state)
		return (
			<Fragment>
				<div className="address-container">
					{items.map(item => item.inputType && this.renderSelect(item))}
				</div>
				<div>
					<Knapp
						className="generate-address"
						type="standard"
						autoDisableVedSpinner
						mini
						spinner={this.state.isFetching}
						onClick={this.onClickHandler}
					>
						Hent gyldig adresse
					</Knapp>
					{this.gyldigeAdresser && this.renderAdresseSelect()}
				</div>
			</Fragment>
		)
	}
}

// ------------------------------------------------------------------------------------

// import React, { Component, Fragment } from 'react'
// import Knapp from 'nav-frontend-knapper'
// import { Radio } from 'nav-frontend-skjema'
// import Lukknapp from 'nav-frontend-lukknapp'
// import Modal from 'react-modal'
// import DollySelect from '~/components/fields/Select/Select'
// import { TpsfApi, DollyApi } from '~/service/Api'

// import './AutofillAddress.less'

// const customStyles = {
// 	content: {
// 		top: '50%',
// 		left: '50%',
// 		right: 'auto',
// 		bottom: 'auto',
// 		marginRight: '-50%',
// 		transform: 'translate(-50%, -50%)',
// 		width: '25%',
// 		minWidth: '500px',
// 		overflow: 'inherit'
// 	}
// }

// Modal.setAppElement('#root')

// const initialState = {
// 	isFetching: false,
// 	type: 'random',
// 	input: '',
// 	status: ''
// }

// export default class AutofillAddress extends Component {
// 	state = { modalOpen: false, ...initialState }

// 	open = () => {
// 		this.setState({ modalOpen: true, ...initialState })
// 	}

// 	close = () => {
// 		this.setState({ modalOpen: false })
// 	}

// 	chooseType = type => {
// 		this.setState({ type, input: '' })
// 	}

// 	onInputChangeHandler = value => {
// 		this.setState({ input: value })
// 	}

// 	placeHolderGenerator = () => {
// 		const { type } = this.state
// 		if (type === 'pnr') return 'Velg postnummer...'
// 		if (type === 'knr') return 'Velg kommunenummer...'
// 		return ''
// 	}

// 	loadOptionsSelector = () => {
// 		const { type } = this.state
// 		if (type === 'pnr') return () => this.fetchKodeverk('Postnummer')
// 		if (type === 'knr') return () => this.fetchKodeverk('Kommuner')
// 		return undefined
// 	}

// 	fetchKodeverk = kodeverkNavn => {
// 		return DollyApi.getKodeverkByNavn(kodeverkNavn).then(res => {
// 			return {
// 				options: res.data.koder.map(kode => ({
// 					label: `${kode.value} - ${kode.label}`,
// 					value: kode.value
// 				}))
// 			}
// 		})
// 	}

// 	setQueryString = () => {
// 		const { input, type } = this.state
// 		const value = input.value
// 		switch (type) {
// 			case 'pnr':
// 				return `&postNr=${value}`
// 			case 'knr':
// 				return `&kommuneNr=${value}`
// 			default:
// 				return ''
// 		}
// 	}

// 	onClickHandler = () => {
// 		const { formikProps } = this.props

// 		return this.setState({ isFetching: true }, async () => {
// 			try {
// 				const generateAddressResponse = await TpsfApi.generateAddress(this.setQueryString())
// 				const addressData = generateAddressResponse.data.response.data1.adrData
// 				const count = parseInt(generateAddressResponse.data.response.data1.antallForekomster)

// 				let status = generateAddressResponse.data.response.status.utfyllendeMelding
// 				let newAddressObject = {
// 					boadresse_gateadresse: '',
// 					boadresse_husnummer: '',
// 					boadresse_kommunenr: '',
// 					boadresse_postnr: ''
// 				}

// 				if (count > 0) {
// 					const { adrnavn, husnrfra, pnr, knr } = addressData

// 					newAddressObject = {
// 						boadresse_gateadresse: adrnavn.toString(),
// 						boadresse_husnummer: husnrfra.toString(),
// 						boadresse_kommunenr: knr.toString(),
// 						boadresse_postnr: pnr.toString()
// 					}
// 					status = ''
// 				}

// 				formikProps.setValues({ ...formikProps.values, ...newAddressObject })

// 				return this.setState({ isFetching: false, modalOpen: false, status })
// 			} catch (err) {
// 				return this.setState({ isFetching: false, modalOpen: false })
// 			}
// 		})
// 	}

// 	renderSelect = () => {
// 		const { type, input } = this.state

// 		const selectProps = {
// 			loadOptions: this.loadOptionsSelector(),
// 			placeholder: this.placeHolderGenerator(),
// 			disabled: type === 'random'
// 		}

// 		return (
// 			<DollySelect
// 				key={type}
// 				name="generator-select"
// 				label="Velg verdi"
// 				onChange={this.onInputChangeHandler}
// 				value={input}
// 				{...selectProps}
// 			/>
// 		)
// 	}

// 	render() {
// 		const { type, status } = this.state

// 		return (
// 			<Fragment>
// 				<div className="generate-address-create">
// 					<Knapp type="standard" mini onClick={this.open}>
// 						Generer gyldig adresse
// 					</Knapp>
// 					{status && <span className="generate-address-create_status">{status}</span>}
// 				</div>

// 				<Modal
// 					style={customStyles}
// 					isOpen={this.state.modalOpen}
// 					onRequestClose={this.close}
// 					shouldCloseOnEsc
// 				>
// 					<div className="generate-address-container">
// 						<form className="generate-address-form">
// 							{this._renderRadioBtn(true, type, 'random', 'Tilfeldig')}
// 							{this._renderRadioBtn(true, type, 'pnr', 'Postnummer')}
// 							{this._renderRadioBtn(true, type, 'knr', 'Kommunenummer')}
// 						</form>
// 						{this.renderSelect()}
// 						<Knapp
// 							className="generate-address"
// 							type="standard"
// 							autoDisableVedSpinner
// 							mini
// 							spinner={this.state.isFetching}
// 							onClick={this.onClickHandler}
// 						>
// 							Generer
// 						</Knapp>
// 						<Lukknapp onClick={this.close} />
// 					</div>
// 				</Modal>
// 			</Fragment>
// 		)
// 	}

// 	_renderRadioBtn = (autoFocus, type, checkedType, label) => {
// 		return (
// 			<Radio
// 				autoFocus={autoFocus ? autoFocus : false}
// 				checked={type === checkedType}
// 				label={label}
// 				name={label}
// 				onChange={() => this.chooseType(checkedType)}
// 			/>
// 		)
// 	}
// }
