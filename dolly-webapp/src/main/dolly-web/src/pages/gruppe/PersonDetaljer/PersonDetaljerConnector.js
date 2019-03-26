import { connect } from 'react-redux'
import PersonDetaljer from './PersonDetaljer'
import DataMapper from '~/service/dataMapper'
import { GET_KRR_TESTBRUKER, GET_SIGRUN_TESTBRUKER, GET_AAREG_TESTBRUKER } from '~/ducks/testBruker'
import { FRIGJOER_TESTBRUKER } from '~/ducks/testBruker'
import { createLoadingSelector } from '~/ducks/loading'

const loadingSelectorKrr = createLoadingSelector(GET_KRR_TESTBRUKER)
const loadingSelectorSigrun = createLoadingSelector(GET_SIGRUN_TESTBRUKER)
const loadingSelectorAareg = createLoadingSelector(GET_AAREG_TESTBRUKER)

const mapStateToProps = (state, ownProps) => ({
	isFetchingKrr: loadingSelectorKrr(state),
	isFetchingSigrun: loadingSelectorSigrun(state),
	isFetchingAareg: loadingSelectorAareg(state),
	personData: DataMapper.getDetailedData(state, ownProps),
	testIdent: state.gruppe.data[0].testidenter.find(
		testIdent => testIdent.ident === ownProps.personId
	),
	bestillinger: state.bestillingStatuser
})

const mapDispatchToProps = (dispatch, ownProps) => {
	return {
		getKrrTestbruker: () => dispatch(GET_KRR_TESTBRUKER(ownProps.personId)),
		getSigrunTestbruker: () => dispatch(GET_SIGRUN_TESTBRUKER(ownProps.personId)),
		getAaregTestbruker: env => dispatch(GET_AAREG_TESTBRUKER(ownProps.personId, env)),
		frigjoerTestbruker: () => dispatch(FRIGJOER_TESTBRUKER(ownProps.personId))
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(PersonDetaljer)
