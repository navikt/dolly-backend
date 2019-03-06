import { connect } from 'react-redux'
import { removeNyOpenAmStatus } from '~/ducks/openAm'
import OpenAmStatus from './OpenAmStatus'

const mapDispatchToProps = (dispatch, ownProps) => ({
	removeNyOpenAmStatus: id => dispatch(removeNyOpenAmStatus(id))
})

export default connect(
	null,
	mapDispatchToProps
)(OpenAmStatus)
