/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;
import java.util.Date;

public final class AuditReport implements Serializable {
	private static final long serialVersionUID = 1L;

	private String  version;
	private Date    date;
	private String  fqdn;
	private String  companyName;
	private String  customerId;
	private String  licenseType;
	private int     numberOfUsersInLicense;
	private Long    licenseExpirationEpoch;
	
	private Integer	totalLicensedUsers;
	private Integer	totalUsers;
	private Integer	totalLockedUsers;
	private Integer	totalTombstonedUsers;
	private Integer averageNumberOfDailyUsersOverLastMonth;
	private Integer	totalActiveUsersInLastHour;
	private Integer	totalActiveUsersInLastFifteenMinutes;
	
	private Integer numberOfUsersUsing2FAinLogin;
	private Integer	totalUsersUsing2FAPasswordReset;

	private Integer	totalAdmins;
	private Integer totalLockedAdmins;
	
	private Integer	totalItems;
	private Integer	totalItemAssignments;
	private Integer	totalAuditRecords;

	private String  documentStorageDirectory;
	private String  featuresEnabled;
	private String  obidosBuild;
	
	public AuditReport() {}

	public String getFqdn()
	{
		return fqdn;
	}
	public void setFqdn(String fqdn)
	{
		this.fqdn = fqdn;
	}

	public String getCompanyName()
	{
		return companyName;
	}
	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getCustomerId()
	{
		return customerId;
	}
	public void setCustomerId(String customerId)
	{
		this.customerId = customerId;
	}

	public Integer getTotalUsers() {
		return totalUsers;
	}
	public void setTotalUsers(Integer totalUsers) {
		this.totalUsers = totalUsers;
	}
	public Integer getTotalAdmins() {
		return totalAdmins;
	}
	public void setTotalAdmins(Integer totalAdmins) {
		this.totalAdmins = totalAdmins;
	}
	public Integer getTotalLockedUsers() {
		return totalLockedUsers;
	}
	public void setTotalLockedUsers(Integer totalLockedUsers) {
		this.totalLockedUsers = totalLockedUsers;
	}
	public Integer getTotalTombstonedUsers() {
		return totalTombstonedUsers;
	}
	public void setTotalTombstonedUsers(Integer totalTombstonedUsers) {
		this.totalTombstonedUsers = totalTombstonedUsers;
	}
	public Integer getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}
	public Integer getTotalItemAssignments() {
		return totalItemAssignments;
	}
	public void setTotalItemAssignments(Integer totalItemAssignments) {
		this.totalItemAssignments = totalItemAssignments;
	}
	public Integer getTotalAuditRecords() {
		return totalAuditRecords;
	}
	public void setTotalAuditRecords(Integer totalAuditRecords) {
		this.totalAuditRecords = totalAuditRecords;
	}
	public Integer getTotalActiveUsersInLastFifteenMinutes() {
		return totalActiveUsersInLastFifteenMinutes;
	}
	public void setTotalActiveUsersInLastFifteenMinutes(Integer totalActiveUsersInLastFifteenMinutes) {
		this.totalActiveUsersInLastFifteenMinutes = totalActiveUsersInLastFifteenMinutes;
	}
	public Integer getTotalActiveUsersInLastHour() {
		return totalActiveUsersInLastHour;
	}
	public void setTotalActiveUsersInLastHour(Integer totalActiveUsersInLastHour) {
		this.totalActiveUsersInLastHour = totalActiveUsersInLastHour;
	}
	public Integer getTotalLicensedUsers() {
		return totalLicensedUsers;
	}
	public void setTotalLicensedUsers(Integer totalLicensedUsers) {
		this.totalLicensedUsers = totalLicensedUsers;
	}
	public Integer getTotalUsersUsing2FAPasswordReset() {
		return totalUsersUsing2FAPasswordReset;
	}
	public void setTotalUsersUsing2FAPasswordReset(Integer totalUsersUsing2FAPasswordReset) {
		this.totalUsersUsing2FAPasswordReset = totalUsersUsing2FAPasswordReset;
	}
	public String getLicenseType()
	{
		return licenseType;
	}
	public void setLicenseType(String licenseType)
	{
		this.licenseType = licenseType;
	}
	public int getNumberOfUsersInLicense()
	{
		return numberOfUsersInLicense;
	}
	public void setNumberOfUsersInLicense(int numberOfUsersInLicense)
	{
		this.numberOfUsersInLicense = numberOfUsersInLicense;
	}
	public Long getLicenseExpirationEpoch()
	{
		return licenseExpirationEpoch;
	}
	public void setLicenseExpirationEpoch(Long licenseExpirationEpoch)
	{
		this.licenseExpirationEpoch = licenseExpirationEpoch;
	}
	public Integer getAverageNumberOfDailyUsersOverLastMonth()
	{
		return averageNumberOfDailyUsersOverLastMonth;
	}
	public void setAverageNumberOfDailyUsersOverLastMonth(Integer averageNumberOfDailyUsersOverLastMonth)
	{
		this.averageNumberOfDailyUsersOverLastMonth = averageNumberOfDailyUsersOverLastMonth;
	}
	public Integer getNumberOfUsersUsing2FAinLogin()
	{
		return numberOfUsersUsing2FAinLogin;
	}
	public void setNumberOfUsersUsing2FAinLogin(Integer numberOfUsersUsing2FAinLogin)
	{
		this.numberOfUsersUsing2FAinLogin = numberOfUsersUsing2FAinLogin;
	}
	public Integer getTotalLockedAdmins()
	{
		return totalLockedAdmins;
	}
	public void setTotalLockedAdmins(Integer totalLockedAdmins)
	{
		this.totalLockedAdmins = totalLockedAdmins;
	}

	public String getDocumentStorageDirectory() 
	{
		return documentStorageDirectory;
	}

	public void setDocumentStorageDirectory(String documentStorageDirectory) 
	{
		this.documentStorageDirectory = documentStorageDirectory;
	}

	public String getFeaturesEnabled()
	{
		return featuresEnabled;
	}
	public void setFeaturesEnabled(String featuresEnabled)
	{
		this.featuresEnabled = featuresEnabled;
	}
	public String getObidosBuild()
	{
		return obidosBuild;
	}
	public void setObidosBuild(String obidosBuild)
	{
		this.obidosBuild = obidosBuild;
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}
}
