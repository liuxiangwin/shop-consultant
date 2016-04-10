ACC.posSelector = {

	_autoload: [
		"bindAll"
	],

	CONSTANTS:
	{
		SELECTORS:
		{
			ID_POS_FORM: '#pos-form',
			ID_SELECT_POS_SELECTOR: '#pos-selector',
			ID_MODAL_POS_SELECTOR: '#pos-selector-modal',
			ID_EDIT_POS: '#pos-selector-edit'
		}
	},

	bindAll: function ()
	{
		var self = this;
		$(document).ready(function() {
			if (ACC.posSelector.isPosSelectorEnabled())
			{
				self.bindPostNewPosSelection();
				self.bindNotifyAboutMissingPos();
				self.bindEditPos();
			}
		});
	},

	bindPostNewPosSelection: function() {
		$(this.CONSTANTS.SELECTORS.ID_SELECT_POS_SELECTOR).change(function()
		{
			this.form.submit();
		})
	},

	bindNotifyAboutMissingPos: function() {
		var selectedPos = $(this.CONSTANTS.SELECTORS.ID_SELECT_POS_SELECTOR).attr('sessionstore');
		if(selectedPos === '' || selectedPos === undefined)
		{
			$.colorbox({inline:true, href: this.CONSTANTS.SELECTORS.ID_MODAL_POS_SELECTOR, closeButton: false, overlayClose: false, escKey: false});
		}
	},

	bindEditPos: function() {
		var self = this;
		$(this.CONSTANTS.SELECTORS.ID_EDIT_POS).click(function() {
			$.colorbox({inline:true, href: self.CONSTANTS.SELECTORS.ID_MODAL_POS_SELECTOR});
		})
	},

	isPosSelectorEnabled: function()
	{
		return $(this.CONSTANTS.SELECTORS.ID_POS_FORM).length > 0;
	}
};