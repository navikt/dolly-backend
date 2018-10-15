import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import { Formik } from 'formik'
import { AttributtManager } from '~/service/Kodeverk'
import DisplayFormikState from '~/utils/DisplayFormikState'
import FormEditor from '~/components/formEditor/FormEditor'

export default class Step2 extends PureComponent {
	static propTypes = {
		identtype: PropTypes.string,
		antall: PropTypes.number,
		selectedAttributeIds: PropTypes.array,
		setValues: PropTypes.func
	}

	constructor(props) {
		super(props)
		this.AttributtManager = new AttributtManager()
		this.AttributtListe = this.AttributtManager.listSelectedAttributesForValueSelection(
			props.selectedAttributeIds
		)
		this.ValidationListe = this.AttributtManager.getValidations(props.selectedAttributeIds)
		this.InitialValues = this.AttributtManager.getInitialValues(
			props.selectedAttributeIds,
			props.values
		)
	}

	submit = values => {
		this.props.setValues({ values })
	}

	onClickPrevious = values => {
		this.props.setValues({ values, goBack: true })
	}

	render() {
		const { identtype, antall } = this.props

		return (
			<div className="bestilling-step2">
				<div className="content-header">
					<Overskrift label="Velg verdier" />
				</div>

				<div className="grunnoppsett">
					<StaticValue header="TYPE" value={identtype} />
					<StaticValue header="ANTALL PERSONER" value={antall.toString()} />
				</div>

				<Formik
					onSubmit={this.submit}
					initialValues={this.InitialValues}
					validationSchema={this.ValidationListe}
					render={formikProps => (
						<Fragment>
							<FormEditor AttributtListe={this.AttributtListe} FormikProps={formikProps} />
							<NavigationConnector
								onClickNext={formikProps.submitForm}
								onClickPrevious={() => this.onClickPrevious(formikProps.values)}
							/>
							{/* <DisplayFormikState {...formikProps} /> */}
						</Fragment>
					)}
				/>
			</div>
		)
	}
}
