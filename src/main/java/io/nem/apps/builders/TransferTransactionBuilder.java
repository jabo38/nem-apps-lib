package io.nem.apps.builders;

import org.nem.core.crypto.Signature;
import org.nem.core.messages.PlainMessage;
import org.nem.core.messages.SecureMessage;
import org.nem.core.model.Account;
import org.nem.core.model.Message;
import org.nem.core.model.MessageTypes;
import org.nem.core.model.TransactionFeeCalculator;
import org.nem.core.model.TransferTransaction;
import org.nem.core.model.TransferTransactionAttachment;
import org.nem.core.model.ncc.NemAnnounceResult;
import org.nem.core.model.primitive.Amount;
import org.nem.core.time.TimeInstant;
import io.nem.apps.factories.AttachmentFactory;
import io.nem.apps.service.Globals;
import io.nem.apps.util.TransactionSenderUtil;

/**
 * The Class TransactionBuilder.
 */
public class TransferTransactionBuilder {

	/**
	 * Instantiates a new transaction builder.
	 */
	private TransferTransactionBuilder() {
	}

	/**
	 * Sender.
	 *
	 * @param sender
	 *            the sender
	 * @return the i sender
	 */
	public static ISender sender(Account sender) {
		return new TransferTransactionBuilder.Builder(sender);
	}

	/**
	 * The Interface ISender.
	 */
	public interface ISender {

		/**
		 * Recipient.
		 *
		 * @param recipient
		 *            the recipient
		 * @return the i recipient
		 */
		IBuild recipient(Account recipient);
	}

	/**
	 * The Interface IBuild.
	 */
	public interface IBuild {

		/**
		 * Version.
		 *
		 * @param version
		 *            the version
		 * @return the i build
		 */
		IBuild version(int version);

		/**
		 * Sign by.
		 *
		 * @param account
		 *            the account
		 * @return the i build
		 */
		IBuild signBy(Account account);

		/**
		 * Time stamp.
		 *
		 * @param timeInstance
		 *            the time instance
		 * @return the i build
		 */
		IBuild timeStamp(TimeInstant timeInstance);

		/**
		 * Fee.
		 *
		 * @param amount
		 *            the amount
		 * @return the i build
		 */
		IBuild fee(Amount amount);

		/**
		 * Fee calculator.
		 *
		 * @param feeCalculator
		 *            the fee calculator
		 * @return the i build
		 */
		IBuild feeCalculator(TransactionFeeCalculator feeCalculator);

		/**
		 * Amount.
		 *
		 * @param amount
		 *            the amount
		 * @return the i build
		 */
		IBuild amount(Amount amount);

		/**
		 * Message.
		 *
		 * @param message
		 *            the message
		 * @param messageType
		 *            the message type
		 * @return the i build
		 */
		IBuild message(String message, int messageType);

		/**
		 * Message.
		 *
		 * @param message
		 *            the message
		 * @param messageType
		 *            the message type
		 * @return the i build
		 */
		IBuild message(byte[] message, int messageType);

		/**
		 * Attachment.
		 *
		 * @param attachment
		 *            the attachment
		 * @return the i build
		 */
		IBuild attachment(TransferTransactionAttachment attachment);

		/**
		 * Deadline.
		 *
		 * @param timeInstant
		 *            the time instant
		 * @return the i build
		 */
		IBuild deadline(TimeInstant timeInstant);

		/**
		 * Signature.
		 *
		 * @param signature
		 *            the signature
		 * @return the i build
		 */
		IBuild signature(Signature signature);

		/**
		 * Builds the transaction.
		 *
		 * @return the transfer transaction
		 */
		TransferTransaction buildTransaction();

		/**
		 * Builds the and send transaction.
		 *
		 * @return the transaction
		 */
		NemAnnounceResult buildAndSendTransaction();
	}

	/**
	 * The Class Builder.
	 */
	private static class Builder implements ISender, IBuild {

		/** The instance. */
		// private SpectroTransaction instance = new SpectroTransaction();
		private TransferTransaction instance;

		/** The version. */
		// constructor
		private int version;

		/** The time stamp. */
		private TimeInstant timeStamp;

		/** The sender. */
		private Account sender;

		/** The recipient. */
		private Account recipient;

		/** The amount. */
		private Amount amount;

		/** The attachment. */
		private TransferTransactionAttachment attachment;

		/** The signature. */
		private Signature signature;

		/** The deadline. */
		private TimeInstant deadline;

		/** The fee. */
		// secondary
		private Amount fee;

		/** The fee calculator. */
		private TransactionFeeCalculator feeCalculator;

		/** The sign by. */
		private Account signBy;

		/**
		 * Instantiates a new builder.
		 *
		 * @param sender
		 *            the sender
		 */
		public Builder(Account sender) {
			this.sender = sender;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * io.nem.spectro.builders.GenericTransactionBuilder.ISender#recipient(
		 * org.nem.core.model.Account)
		 */
		@Override
		public IBuild recipient(Account recipient) {
			this.recipient = recipient;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.nem.builders.TransactionBuilder.IRecipient#amount(java.lang.
		 * Long)
		 */
		@Override
		public IBuild amount(Amount amount) {
			this.amount = amount;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.nem.builders.TransactionBuilder.IBuild#attachment(org.nem.
		 * core.model.TransferTransactionAttachment)
		 */
		@Override
		public IBuild attachment(TransferTransactionAttachment attachment) {
			this.attachment = attachment;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.nem.builders.TransactionBuilder.IBuild#buildTransaction()
		 */
		@Override
		public TransferTransaction buildTransaction() {
			if (this.timeStamp == null) {
				this.timeStamp = Globals.TIME_PROVIDER.getCurrentTime();
			}
			
			if(this.amount == null) {
				this.amount(Amount.fromNem(0));
			}
			
			if (this.version == 0) {
				instance = new TransferTransaction(this.timeStamp, this.sender, this.recipient, this.amount,
						this.attachment);
			} else {
				instance = new TransferTransaction(this.version, this.timeStamp, this.sender, this.recipient,
						this.amount, this.attachment);
			}

			if (this.fee == null && this.feeCalculator == null) {
				instance.setFee(Amount.fromNem(0));
			} else {

				if (this.fee != null) {
					instance.setFee(Amount.fromNem(0));
				} else if (this.feeCalculator != null) {
					TransactionFeeCalculator feeCalculator;
					if (this.feeCalculator != null) {
						feeCalculator = this.feeCalculator;
					} else {
						feeCalculator = Globals.getGlobalTransactionFee();
					}
					instance.setFee(feeCalculator.calculateMinimumFee(instance));
				}

			}

			if (this.deadline != null) {
				instance.setDeadline(this.deadline);
			} else {
				instance.setDeadline(this.timeStamp.addHours(23));
			}
			if (this.signature != null) {
				instance.setSignature(this.signature);
			}
			if (this.signBy != null) {
				instance.signBy(this.signBy);
			}
			instance.sign();
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.nem.builders.TransactionBuilder.IBuild#
		 * buildAndSendTransaction()
		 */
		@Override
		public NemAnnounceResult buildAndSendTransaction() {
			return TransactionSenderUtil.sendTransferTransaction(this.buildTransaction());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.nem.builders.TransactionBuilder.IBuild#fee(org.nem.core.model
		 * .primitive.Amount)
		 */
		@Override
		public IBuild fee(Amount amount) {
			this.fee = amount;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.nem.builders.TransactionBuilder.IBuild#deadline(org.nem.core.
		 * time.TimeInstant)
		 */
		@Override
		public IBuild deadline(TimeInstant deadline) {
			this.deadline = deadline;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.nem.builders.TransactionBuilder.IBuild#signature(org.nem.core
		 * .crypto.Signature)
		 */
		@Override
		public IBuild signature(Signature signature) {
			this.signature = signature;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * io.nem.spectro.builders.TransactionBuilder.IBuild#message(java.lang.
		 * String, org.nem.core.model.MessageTypes)
		 */
		@Override
		public IBuild message(String message, int messageType) {
			Message transactionMessage = null;
			if (messageType == MessageTypes.SECURE) {
				transactionMessage = SecureMessage.fromDecodedPayload(this.sender, this.recipient, message.getBytes());
			} else {
				transactionMessage = new PlainMessage(message.getBytes());
			}

			if (this.attachment == null) {
				this.attachment = (AttachmentFactory.createTransferTransactionAttachment(transactionMessage));
			} else {
				this.attachment.setMessage(transactionMessage);
			}

			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * io.nem.spectro.builders.TransactionBuilder.IBuild#message(byte[],
		 * org.nem.core.model.MessageTypes)
		 */
		@Override
		public IBuild message(byte[] message, int messageType) {
			Message transactionMessage = null;
			if (messageType == MessageTypes.SECURE) {
				transactionMessage = SecureMessage.fromDecodedPayload(this.sender, this.recipient, message);
			} else {
				transactionMessage = new PlainMessage(message);
			}

			if (this.attachment == null) {
				this.attachment = (AttachmentFactory.createTransferTransactionAttachment(transactionMessage));
			} else {
				this.attachment.setMessage(transactionMessage);
			}

			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see io.nem.spectro.builders.GenericTransactionBuilder.IBuild#
		 * feeCalculator(org.nem.core.model.TransactionFeeCalculator)
		 */
		@Override
		public IBuild feeCalculator(TransactionFeeCalculator feeCalculator) {
			this.feeCalculator = feeCalculator;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * io.nem.apps.builders.TransferTransactionBuilder.IBuild#version(int)
		 */
		@Override
		public IBuild version(int version) {
			this.version = version;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * io.nem.apps.builders.TransferTransactionBuilder.IBuild#timeStamp(org.
		 * nem.core.time.TimeInstant)
		 */
		@Override
		public IBuild timeStamp(TimeInstant timeInstance) {
			this.timeStamp = timeInstance;
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * io.nem.apps.builders.TransferTransactionBuilder.IBuild#signBy(org.nem
		 * .core.model.Account)
		 */
		@Override
		public IBuild signBy(Account account) {
			this.signBy = account;
			return this;
		}

	}

}
